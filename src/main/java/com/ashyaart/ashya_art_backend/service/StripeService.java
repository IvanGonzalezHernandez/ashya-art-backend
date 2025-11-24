package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.Carrito;
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
import com.ashyaart.ashya_art_backend.repository.CarritoDao;
import com.ashyaart.ashya_art_backend.repository.CompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.repository.ProductoCompraDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;
import com.ashyaart.ashya_art_backend.repository.SecretoCompraDao;
import com.ashyaart.ashya_art_backend.repository.SecretoDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.Coupon;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraTotalConfirmadaEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CursoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.ProductoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.SecretoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.TarjetaRegaloCompradaEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraStripeAdminSuccessEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraStripeAdminErrorEvent;



@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Autowired private StockService stockService;
    @Autowired private ClienteService clienteService;
    @Autowired private CursoCompraDao cursoCompraDao;
    @Autowired private CursoFechaDao cursoFechaDao;
    @Autowired private CompraDao compraDao;
    @Autowired private ProductoDao productoDao;
    @Autowired private ProductoCompraDao productoCompraDao;
    @Autowired private SecretoDao secretoDao;
    @Autowired private SecretoCompraDao secretoCompraDao;
    @Autowired private TarjetaRegaloDao tarjetaRegaloDao;
    @Autowired private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;
    @Autowired private CarritoDao carritoDao;
    @Autowired private ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StripeService() {
        Stripe.apiKey = System.getenv("STRIPE_TEST_KEY");
        logger.info("Stripe API Key configurada");
    }

    public String crearSesion(CarritoClienteDto carritoClienteDto, String successUrl, String cancelUrl) throws Exception {
        // Extraer carrito y cliente
        CarritoDto carritoDto = carritoClienteDto.getCarrito();
        ClienteDto clienteDto = carritoClienteDto.getCliente();

        // Generar referencia y guardar el contexto (evita metadata > 500 chars)
        String checkoutRef = UUID.randomUUID().toString().replace("-", "");
        carritoDto.setId(checkoutRef);
        Carrito carrito = new Carrito();
        carrito.setId(checkoutRef);
        carrito.setClienteJson(objectMapper.writeValueAsString(clienteDto));
        carrito.setCarritoJson(objectMapper.writeValueAsString(carritoDto));
        String codigoTarjeta = null;
        if (carritoClienteDto.getCodigoTarjeta() != null && !carritoClienteDto.getCodigoTarjeta().isBlank()) {
            codigoTarjeta = carritoClienteDto.getCodigoTarjeta().trim().toUpperCase();
        }
        carritoDao.save(carrito);

        logger.info("Creando sesión Stripe para cliente: {}", clienteDto.getEmail());
        logger.info("Carrito contiene {} items", carritoDto.getItems().size());

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // 1) Validar stock/plazas
        for (ItemCarritoDto item : carritoDto.getItems()) {
            logger.info("Validando stock para item: {} - Cantidad: {}", item.getNombre(), item.getCantidad());
            if (!stockService.hayStockSuficiente(item)) {
                logger.warn("No hay suficiente stock/plazas para {}", item.getNombre());
                throw new IllegalArgumentException("No hay suficiente stock o plazas para: " + item.getNombre());
            }
        }

        // 2) Descuento por tarjeta regalo
        BigDecimal descuento = BigDecimal.ZERO;
        Coupon coupon = null;

        if (codigoTarjeta != null) {
            Optional<TarjetaRegaloCompra> optionalTarjeta = tarjetaRegaloCompraDao.findByCodigo(codigoTarjeta);
            if (optionalTarjeta.isPresent()) {
                TarjetaRegaloCompra tarjeta = optionalTarjeta.get();
                if (tarjeta.isEstado() && !tarjeta.isCanjeada() && tarjeta.getFechaCaducidad().isAfter(LocalDate.now())) {
                    descuento = tarjeta.getTarjetaRegalo().getPrecio();
                    Map<String, Object> couponParams = new HashMap<>();
                    couponParams.put("amount_off", descuento.multiply(BigDecimal.valueOf(100)).longValue());
                    couponParams.put("currency", "eur");
                    coupon = Coupon.create(couponParams);
                } else {
                    codigoTarjeta = null;
                }
            } else {
                codigoTarjeta = null;
            }
        }


        // 3) Line Items de Stripe
        for (ItemCarritoDto item : carritoDto.getItems()) {
        	String name = Optional.ofNullable(safeClip(item.getNombre(), 250))
                    .filter(s -> !s.isBlank())
                    .orElse("Artículo");
            String desc = safeClip(item.getSubtitulo(), 500);

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity((long) item.getCantidad())
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount(Math.round(item.getPrecio() * 100))
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(name)
                                .setDescription(desc)
                                .build()
                        ).build()
                ).build();

            lineItems.add(lineItem);
        }

        // 4) Construir params (sin JSON en metadata)
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
        	    .addAllLineItem(lineItems)
        	    .setMode(SessionCreateParams.Mode.PAYMENT)
        	    .setSuccessUrl(successUrl)
        	    .setCancelUrl(cancelUrl)
        	    .setClientReferenceId(checkoutRef);

        	if (clienteDto.getEmail() != null && !clienteDto.getEmail().isBlank()) {
        	    paramsBuilder.setCustomerEmail(clienteDto.getEmail());
        	}

        	if (coupon != null) {
        	    paramsBuilder.addDiscount(
        	        SessionCreateParams.Discount.builder().setCoupon(coupon.getId()).build()
        	    );
        	    paramsBuilder.putMetadata("codigoTarjeta", codigoTarjeta);
        	}


        SessionCreateParams params = paramsBuilder.build();

        logger.info("Creando sesión en Stripe...");
        Session session = Session.create(params);
        logger.info("Sesión Stripe creada. URL: {}", session.getUrl());

        return session.getUrl();
    }

    private static String safeClip(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    // ---------------------
    // PROCESADO DE COMPRA
    // ---------------------
    @Transactional
    public void procesarSesionStripe(ClienteDto clienteDto, CarritoDto carritoDto) {
        try {
            if (carritoDto.getId() == null || carritoDto.getId().isBlank()) {
                throw new IllegalStateException("CarritoDto sin ID, no es posible procesar");
            }
            // Comprobar si ya hay una compra para este carrito
            Optional<Compra> existente = compraDao.findByCarritoId(carritoDto.getId());
            if (existente.isPresent()) {
                logger.info("Compra ya procesada para carrito {}", carritoDto.getId());
                return;
            }

            Cliente cliente = clienteService.crearActualizarCliente(clienteDto);

            BigDecimal total = carritoDto.getItems().stream()
                .map(i -> BigDecimal.valueOf(i.getPrecio()).multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Compra compraTotal = new Compra();
            compraTotal.setCarritoId(carritoDto.getId());
            compraTotal.setCliente(cliente);
            compraTotal.setCodigoCompra(UUID.randomUUID().toString());
            compraTotal.setFechaCompra(LocalDate.now());
            compraTotal.setTotal(total);
            compraTotal.setPagado(true);
            compraDao.save(compraTotal);
            logger.info("Compra registrada con ID: {}", compraTotal.getId());

            // Email cliente (ya lo tenías)
            eventPublisher.publishEvent(
                new CompraTotalConfirmadaEvent(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    compraTotal
                )
            );

            // Items del carrito
            for (ItemCarritoDto item : carritoDto.getItems()) {
                try {
                    switch (item.getTipo().toUpperCase()) {
                        case "CURSO":
                            procesarCurso(cliente, compraTotal, item);
                            break;
                        case "PRODUCTO":
                            procesarProducto(cliente, compraTotal, item);
                            break;
                        case "TARJETA":
                            procesarTarjetaRegalo(cliente, compraTotal, item);
                            break;
                        case "SECRETO":
                            procesarSecreto(cliente, compraTotal, item);
                            break;
                        default:
                            logger.warn("Tipo de item desconocido: {}", item.getTipo());
                    }
                } catch (Exception e) {
                    logger.error("Error procesando item del carrito: {}", item, e);
                    throw e; // forzamos rollback
                }
            }

            // ✅ Notificación admin – Stripe SUCCESS
            eventPublisher.publishEvent(
                new CompraStripeAdminSuccessEvent(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    compraTotal
                )
            );

        } catch (Exception e) {
            logger.error("Error procesando sesión Stripe para {}: {}", clienteDto.getEmail(), e.getMessage(), e);

            // ⚠️ Notificación admin – Stripe ERROR
            eventPublisher.publishEvent(
                new CompraStripeAdminErrorEvent(
                    clienteDto.getEmail(),
                    clienteDto.getNombre(),
                    e.getMessage()
                )
            );

            throw e; // importante: re-lanzar para rollback
        }
    }


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

        cursoFecha.setPlazasDisponibles(cursoFecha.getPlazasDisponibles() - item.getCantidad());
        cursoFechaDao.save(cursoFecha);

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

        producto.setStock(producto.getStock() - item.getCantidad());
        productoDao.save(producto);

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

            TarjetaRegaloCompra tarjetaCompra = new TarjetaRegaloCompra();
            tarjetaCompra.setCodigo(codigoUnico);
            tarjetaCompra.setDestinatario(item.getDestinatario());
            tarjetaCompra.setTarjetaRegalo(plantilla);
            tarjetaCompra.setCliente(cliente);
            tarjetaCompra.setCompra(compraTotal);
            tarjetaCompra.setCanjeada(false);
            tarjetaCompra.setEstado(true);
            tarjetaCompra.setFechaCompra(LocalDate.now());
            tarjetaCompra.setFechaCaducidad(LocalDate.now().plusMonths(6));
            tarjetaRegaloCompraDao.save(tarjetaCompra);

            eventPublisher.publishEvent(
                    new TarjetaRegaloCompradaEvent(
                        cliente.getEmail(),
                        cliente.getNombre(),
                        item.getDestinatario(),
                        codigoUnico,
                        plantilla.getPrecio(),
                        tarjetaCompra.getFechaCaducidad()
                    )
             );
        }
    }
}
