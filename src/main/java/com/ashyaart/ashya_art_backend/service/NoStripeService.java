package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.mail.MessagingException;

@Service
public class NoStripeService {

    private static final Logger logger = LoggerFactory.getLogger(NoStripeService.class);

    @Autowired private StockService stockService;
    @Autowired private ClienteService clienteService;
    @Autowired private EmailService emailService;

    @Autowired private CompraDao compraDao;
    @Autowired private CursoFechaDao cursoFechaDao;
    @Autowired private CursoCompraDao cursoCompraDao;
    @Autowired private ProductoDao productoDao;
    @Autowired private ProductoCompraDao productoCompraDao;
    @Autowired private SecretoDao secretoDao;
    @Autowired private SecretoCompraDao secretoCompraDao;
    @Autowired private TarjetaRegaloDao tarjetaRegaloDao;
    @Autowired private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;

    /**
     * Procesa una compra cubierta al 100% por tarjeta regalo, sin pasar por Stripe.
     */
    @Transactional
    public String procesarCompraSinStripe(CarritoClienteDto carritoClienteDto) throws Exception {

        // 1️ Extraer datos del DTO
        CarritoDto carrito = carritoClienteDto.getCarrito();
        ClienteDto clienteDto = carritoClienteDto.getCliente();
        String codigoTarjeta = (carritoClienteDto.getCodigoTarjeta() != null)
                ? carritoClienteDto.getCodigoTarjeta().trim().toUpperCase()
                : null;

        if (codigoTarjeta == null || codigoTarjeta.isBlank()) {
            throw new IllegalArgumentException("Debe especificarse un código de tarjeta regalo válido.");
        }

        // 2️ Validar stock/plazas
        for (ItemCarritoDto item : carrito.getItems()) {
            if (!stockService.hayStockSuficiente(item)) {
                throw new IllegalArgumentException("No hay suficiente stock o plazas para: " + item.getNombre());
            }
        }

        // 3️ Calcular total del carrito
        BigDecimal total = carrito.getItems().stream()
                .map(i -> BigDecimal.valueOf(i.getPrecio())
                        .multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4️ Validar tarjeta regalo
        Optional<TarjetaRegaloCompra> optionalTarjeta = tarjetaRegaloCompraDao.findByCodigo(codigoTarjeta);
        if (optionalTarjeta.isEmpty()) {
            throw new IllegalArgumentException("Tarjeta regalo no encontrada.");
        }

        TarjetaRegaloCompra tarjetaCompra = optionalTarjeta.get();
        TarjetaRegalo plantilla = tarjetaCompra.getTarjetaRegalo();

        if (!tarjetaCompra.isEstado() || tarjetaCompra.isCanjeada()
                || tarjetaCompra.getFechaCaducidad() == null
                || !tarjetaCompra.getFechaCaducidad().isAfter(LocalDate.now())) {
            throw new IllegalStateException("La tarjeta regalo no está activa o ha caducado.");
        }

        if (plantilla.getPrecio().compareTo(total) < 0) {
            throw new IllegalArgumentException("La tarjeta regalo no cubre el importe total del carrito.");
        }

        // 5️ Crear o actualizar cliente
        Cliente cliente = clienteService.crearActualizarCliente(clienteDto);

        // 6️ Crear compra pagada sin Stripe
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setCodigoCompra(UUID.randomUUID().toString());
        compra.setFechaCompra(LocalDate.now());
        compra.setTotal(total);
        compra.setPagado(true);
        // compra.setMetodoPago("TARJETA_REGALO"); // solo si tu entidad tiene ese campo
        compraDao.save(compra);
        logger.info("Compra sin Stripe creada para {} (total {}€)", cliente.getEmail(), total);

        // 7️ Procesar los ítems del carrito
        for (ItemCarritoDto item : carrito.getItems()) {
            try {
                switch (item.getTipo().toUpperCase()) {
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
                        logger.warn("Tipo de item desconocido: {}", item.getTipo());
                }
            } catch (Exception e) {
                logger.error("Error procesando item {}: {}", item.getNombre(), e.getMessage());
            }
        }

        // 8️ Marcar la tarjeta como usada
        tarjetaRegaloCompraDao.marcarTarjetaRegaloComoUsada(codigoTarjeta);
        logger.info("Tarjeta regalo {} marcada como usada", codigoTarjeta);

        // 9️ Enviar email de confirmación general
        emailService.enviarConfirmacionCompraTotal(cliente.getEmail(), cliente.getNombre(), compra);

        return compra.getCodigoCompra();
    }

    // ============================
    // Métodos auxiliares (idénticos a StripeService)
    // ============================

    private void procesarCurso(Cliente cliente, Compra compraTotal, ItemCarritoDto item) throws MessagingException {
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

        cursoFecha.setPlazasDisponibles(cursoFecha.getPlazasDisponibles() - item.getCantidad());
        cursoFechaDao.save(cursoFecha);

        emailService.enviarConfirmacionCursoIndividual(
                cliente.getEmail(),
                cliente.getNombre(),
                cursoFecha.getCurso().getNombre(),
                cursoFecha.getFecha().toString(),
                cursoFecha.getHoraInicio().toString(),
                item.getCantidad(),
                cursoFecha.getCurso().getPrecio(),
                cursoFecha.getCurso().getInformacionExtra()
        );
    }

    private void procesarProducto(Cliente cliente, Compra compraTotal, ItemCarritoDto item) throws MessagingException {
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

        producto.setStock(producto.getStock() - item.getCantidad());
        productoDao.save(producto);

        emailService.enviarConfirmacionProductoIndividual(
                cliente.getEmail(), cliente.getNombre(),
                producto.getNombre(), item.getCantidad(), producto.getPrecio()
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

        emailService.enviarConfirmacionSecretoIndividual(
                cliente.getEmail(), cliente.getNombre(),
                secreto.getNombre(), secreto.getPdf()
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

            emailService.enviarConfirmacionTarjetaRegaloIndividual(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    item.getDestinatario(),
                    codigoUnico,
                    plantilla.getPrecio(),
                    nueva.getFechaCaducidad()
            );
        }
    }
}