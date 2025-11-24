package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Compra;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.entity.ProductoCompra;
import com.ashyaart.ashya_art_backend.entity.Secreto;
import com.ashyaart.ashya_art_backend.entity.SecretoCompra;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraTotalConfirmadaEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CursoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.ProductoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.SecretoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.TarjetaRegaloCompradaEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraNoStripeAdminEvent;
import com.ashyaart.ashya_art_backend.model.CarritoClienteDto;
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.ashyaart.ashya_art_backend.repository.CompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.repository.ProductoCompraDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;
import com.ashyaart.ashya_art_backend.repository.SecretoCompraDao;
import com.ashyaart.ashya_art_backend.repository.SecretoDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloDao;

@Service
public class NoStripeService {

    private static final Logger logger = LoggerFactory.getLogger(NoStripeService.class);

    @Autowired private StockService stockService;
    @Autowired private ClienteService clienteService;

    @Autowired private CompraDao compraDao;
    @Autowired private CursoFechaDao cursoFechaDao;
    @Autowired private CursoCompraDao cursoCompraDao;
    @Autowired private ProductoDao productoDao;
    @Autowired private ProductoCompraDao productoCompraDao;
    @Autowired private SecretoDao secretoDao;
    @Autowired private SecretoCompraDao secretoCompraDao;
    @Autowired private TarjetaRegaloDao tarjetaRegaloDao;
    @Autowired private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;

    @Autowired private ApplicationEventPublisher eventPublisher;

    /**
     * Procesa una compra cubierta al 100% por tarjeta regalo, sin pasar por Stripe.
     * También permite compras gratuitas (total = 0) sin tarjeta regalo.
     * Siempre lanza un evento para notificar al administrador (éxito o error).
     */
    @Transactional
    public String procesarCompraSinStripe(CarritoClienteDto carritoClienteDto) throws Exception {

        // Los dejo fuera del try para poder usarlos también en el catch
        final CarritoDto carrito = carritoClienteDto.getCarrito();
        final ClienteDto clienteDto = carritoClienteDto.getCliente();
        final String codigoTarjeta = (carritoClienteDto.getCodigoTarjeta() != null)
                ? carritoClienteDto.getCodigoTarjeta().trim().toUpperCase()
                : null;

        try {
            // 1) Validar stock/plazas
            for (ItemCarritoDto item : carrito.getItems()) {
                if (!stockService.hayStockSuficiente(item)) {
                    throw new IllegalArgumentException("No hay suficiente stock o plazas para: " + item.getNombre());
                }
            }

            // 2) Calcular total del carrito
            BigDecimal total = carrito.getItems().stream()
                    .map(i -> BigDecimal.valueOf(i.getPrecio())
                            .multiply(BigDecimal.valueOf(i.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean compraGratuita = total.compareTo(BigDecimal.ZERO) == 0;

            // 3) Validar tarjeta regalo SOLO si total > 0
            TarjetaRegaloCompra tarjetaCompra = null;
            TarjetaRegalo plantilla = null;

            if (!compraGratuita) {
                if (codigoTarjeta == null || codigoTarjeta.isBlank()) {
                    throw new IllegalArgumentException("Debe especificarse un código de tarjeta regalo válido.");
                }

                Optional<TarjetaRegaloCompra> optionalTarjeta = tarjetaRegaloCompraDao.findByCodigo(codigoTarjeta);
                if (optionalTarjeta.isEmpty()) {
                    throw new IllegalArgumentException("Tarjeta regalo no encontrada.");
                }

                tarjetaCompra = optionalTarjeta.get();
                plantilla = tarjetaCompra.getTarjetaRegalo();

                if (!tarjetaCompra.isEstado() || tarjetaCompra.isCanjeada()
                        || tarjetaCompra.getFechaCaducidad() == null
                        || !tarjetaCompra.getFechaCaducidad().isAfter(LocalDate.now())) {
                    throw new IllegalStateException("La tarjeta regalo no está activa o ha caducado.");
                }

                if (plantilla.getPrecio().compareTo(total) < 0) {
                    throw new IllegalArgumentException("La tarjeta regalo no cubre el importe total del carrito.");
                }
            }

            // 4) Crear o actualizar cliente
            Cliente cliente = clienteService.crearActualizarCliente(clienteDto);

            // 5) Crear compra pagada sin Stripe
            Compra compra = new Compra();
            compra.setCliente(cliente);
            compra.setCodigoCompra(UUID.randomUUID().toString());
            compra.setFechaCompra(LocalDate.now());
            compra.setTotal(total);
            compra.setPagado(true);
            compraDao.save(compra);
            logger.info("Compra sin Stripe creada para {} (total {}€)", cliente.getEmail(), total);

            // Evento cliente: compra total confirmada
            eventPublisher.publishEvent(
                new CompraTotalConfirmadaEvent(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    compra
                )
            );

            // 6) Procesar los ítems del carrito
            for (ItemCarritoDto item : carrito.getItems()) {
                try {
                    String tipo = Optional.ofNullable(item.getTipo())
                                          .map(String::toUpperCase)
                                          .orElse("");

                    switch (tipo) {
                        case "CURSO":
                            procesarCurso(cliente, compra, item);
                            break;
                        case "PRODUCTO":
                            procesarProducto(cliente, compra, item);
                            break;
                        case "TARJETA":
                            procesarTarjetaRegalo(cliente, compra, item);
                            break;
                        case "SECRETO":
                            procesarSecreto(cliente, compra, item);
                            break;
                        default:
                            logger.warn("Tipo de item desconocido o vacío: {}", item.getTipo());
                    }
                } catch (Exception e) {
                    logger.error("Error procesando item {}: {}", item.getNombre(), e.getMessage(), e);
                    throw e; // rollback
                }
            }

            // 7) Marcar la tarjeta como usada SOLO si no es compra gratuita
            if (!compraGratuita && codigoTarjeta != null) {
                tarjetaRegaloCompraDao.marcarTarjetaRegaloComoUsada(codigoTarjeta);
                logger.info("Tarjeta regalo {} marcada como usada", codigoTarjeta);
            } else if (compraGratuita) {
                logger.info("Compra gratuita procesada — no se ha usado tarjeta regalo");
            }

            // 8) Notificar al admin (éxito)
            eventPublisher.publishEvent(
                new CompraNoStripeAdminEvent(
                    "COMPRA_SIN_STRIPE",
                    true,
                    null,
                    clienteDto,
                    carrito,
                    compra
                )
            );

            return compra.getCodigoCompra();

        } catch (Exception e) {
            logger.error("Error en procesarCompraSinStripe (NoStripeService): {}", e.getMessage(), e);

            // Notificar al admin (error)
            eventPublisher.publishEvent(
                new CompraNoStripeAdminEvent(
                    "COMPRA_SIN_STRIPE",
                    false,
                    e.getMessage(),
                    clienteDto,
                    carrito,
                    null
                )
            );

            throw e;
        }
    }


    /**
     * Registra una reserva para pagar en el Atelier (sin Stripe).
     * - Solo admite cursos.
     * - Descuenta plazas.
     * - Marca la compra como NO pagada (pagado = false).
     */
    @Transactional
    public String procesarReservaAtelier(CarritoClienteDto carritoClienteDto) throws Exception {

        // 1) Extraer datos del DTO
        CarritoDto carrito = carritoClienteDto.getCarrito();
        ClienteDto clienteDto = carritoClienteDto.getCliente();

        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        // 2) Validar que solo haya cursos y que haya plazas
        for (ItemCarritoDto item : carrito.getItems()) {

            if (!"CURSO".equalsIgnoreCase(item.getTipo())) {
                throw new IllegalArgumentException("Solo se pueden reservar cursos para pagar en el Atelier.");
            }

            if (!stockService.hayStockSuficiente(item)) {
                throw new IllegalArgumentException("No hay plazas suficientes para: " + item.getNombre());
            }
        }

        // 3) Calcular total del carrito
        BigDecimal total = carrito.getItems().stream()
                .map(i -> BigDecimal.valueOf(i.getPrecio())
                        .multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4) Crear o actualizar cliente
        Cliente cliente = clienteService.crearActualizarCliente(clienteDto);

        // 5) Crear compra con pagado = false
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setCodigoCompra(UUID.randomUUID().toString());
        compra.setFechaCompra(LocalDate.now());
        compra.setTotal(total);
        compra.setPagado(false); // NO pagado (reserva)
        compraDao.save(compra);

        logger.info("Reserva Atelier creada para {} (total {}€)", cliente.getEmail(), total);

        // Publicamos evento de compra/reserva total (email adaptará texto según pagado=true/false)
        eventPublisher.publishEvent(
            new CompraTotalConfirmadaEvent(
                cliente.getEmail(),
                cliente.getNombre(),
                compra
            )
        );

        // 6) Procesar items (cursos)
        for (ItemCarritoDto item : carrito.getItems()) {
            try {
                procesarCurso(cliente, compra, item);
            } catch (Exception e) {
                logger.error("Error procesando curso {}: {}", item.getNombre(), e.getMessage(), e);
                throw e; // rollback
            }
        }

        return compra.getCodigoCompra();
    }

    // ============================
    // Métodos auxiliares
    // ============================

    private void procesarCurso(Cliente cliente, Compra compraTotal, ItemCarritoDto item) {

        Long idCursoFecha = Long.valueOf(item.getId());
        CursoFecha cursoFecha = cursoFechaDao.findById(idCursoFecha)
                .orElseThrow(() -> new RuntimeException("CursoFecha no encontrada: " + idCursoFecha));

        CursoCompra compra = new CursoCompra();
        compra.setCompra(compraTotal);
        compra.setCursoFecha(cursoFecha);
        compra.setCliente(cliente);
        compra.setPlazasReservadas(item.getCantidad());
        compra.setFechaReserva(LocalDateTime.now());
        cursoCompraDao.save(compra);

        // Actualizar plazas disponibles
        cursoFecha.setPlazasDisponibles(cursoFecha.getPlazasDisponibles() - item.getCantidad());
        cursoFechaDao.save(cursoFecha);

        // Evento
        eventPublisher.publishEvent(
            new CursoCompradoEvent(
                cliente.getEmail(),
                cliente.getNombre(),
                cursoFecha.getCurso().getNombre(),
                cursoFecha.getFecha(),
                cursoFecha.getHoraInicio().toString(),
                item.getCantidad(),
                cursoFecha.getCurso().getPrecio(),
                cursoFecha.getCurso().getInformacionExtra()
            )
        );
    }

    private void procesarProducto(Cliente cliente, Compra compraTotal, ItemCarritoDto item) {

        Long idProducto = Long.valueOf(item.getId());
        Producto producto = productoDao.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

        ProductoCompra compra = new ProductoCompra();
        compra.setCompra(compraTotal);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setCliente(cliente);
        compra.setProducto(producto);
        compra.setCantidad(item.getCantidad());
        productoCompraDao.save(compra);

        // Actualizar stock
        producto.setStock(producto.getStock() - item.getCantidad());
        productoDao.save(producto);

        // Evento
        eventPublisher.publishEvent(
            new ProductoCompradoEvent(
                cliente.getEmail(),
                cliente.getNombre(),
                producto.getNombre(),
                item.getCantidad(),
                producto.getPrecio()
            )
        );
    }

    private void procesarSecreto(Cliente cliente, Compra compraTotal, ItemCarritoDto item) {

        Long idSecreto = Long.valueOf(item.getId());
        Secreto secreto = secretoDao.findById(idSecreto)
                .orElseThrow(() -> new RuntimeException("Secreto no encontrado: " + idSecreto));

        SecretoCompra compra = new SecretoCompra();
        compra.setCompra(compraTotal);
        compra.setFechaCompra(LocalDate.now());
        compra.setCliente(cliente);
        compra.setSecreto(secreto);
        secretoCompraDao.save(compra);

        byte[] pdfBytes = secreto.getPdf();

        // Evento
        eventPublisher.publishEvent(
            new SecretoCompradoEvent(
                cliente.getEmail(),
                cliente.getNombre(),
                secreto.getNombre(),
                pdfBytes
            )
        );
    }

    private void procesarTarjetaRegalo(Cliente cliente, Compra compraTotal, ItemCarritoDto item) {

        Long idTarjeta = Long.valueOf(item.getId());
        TarjetaRegalo plantilla = tarjetaRegaloDao.findById(idTarjeta)
                .orElseThrow(() -> new RuntimeException("Tarjeta Regalo no encontrada: " + idTarjeta));

        for (int i = 0; i < item.getCantidad(); i++) {
            String codigoUnico = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

            TarjetaRegaloCompra nueva = new TarjetaRegaloCompra();
            nueva.setCodigo(codigoUnico);
            nueva.setDestinatario(item.getDestinatario());
            nueva.setTarjetaRegalo(plantilla);
            nueva.setCliente(cliente);
            nueva.setCompra(compraTotal);
            nueva.setCanjeada(false);
            nueva.setEstado(true);
            nueva.setFechaCompra(LocalDate.now());
            nueva.setFechaCaducidad(LocalDate.now().plusMonths(6));
            tarjetaRegaloCompraDao.save(nueva);

            // Evento
            eventPublisher.publishEvent(
                new TarjetaRegaloCompradaEvent(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    item.getDestinatario(),
                    codigoUnico,
                    plantilla.getPrecio(),
                    nueva.getFechaCaducidad()
                )
            );
        }
    }
}
