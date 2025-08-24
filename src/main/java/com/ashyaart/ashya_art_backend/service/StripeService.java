package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Compra;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.entity.ProductoCompra;
import com.ashyaart.ashya_art_backend.entity.Secreto;
import com.ashyaart.ashya_art_backend.entity.SecretoCompra;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Autowired
    private StockService stockService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private CursoCompraDao cursoCompraDao;
    @Autowired
    private CursoFechaDao cursoFechaDao;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CompraDao compraDao;
    @Autowired
    private ProductoDao productoDao;
    @Autowired
    private ProductoCompraDao productoCompraDao;
    @Autowired
    private SecretoDao secretoDao;
    @Autowired
    private SecretoCompraDao secretoCompraDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StripeService() {
        Stripe.apiKey = System.getenv("STRIPE_TEST_KEY");
        logger.info("Stripe API Key configurada");
    }

    public String crearSesion(CarritoClienteDto carritoClienteDto, String successUrl, String cancelUrl) throws Exception {
        // Extraer carrito y cliente
        CarritoDto carritoDto = carritoClienteDto.getCarrito();
        ClienteDto clienteDto = carritoClienteDto.getCliente();

        logger.info("Creando sesión Stripe para cliente: {}", clienteDto.getEmail());
        logger.info("Carrito contiene {} items", carritoDto.getItems().size());

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // 1. Validar stock/plazas
        for (ItemCarritoDto item : carritoDto.getItems()) {
            logger.info("Validando stock para item: {} - Cantidad: {}", item.getNombre(), item.getCantidad());
            if (!stockService.hayStockSuficiente(item)) {
                logger.warn("No hay suficiente stock o plazas para el item: {}", item.getNombre());
                throw new IllegalArgumentException(
                    "No hay suficiente stock o plazas para el item: " + item.getNombre()
                );
            }
        }

        // 2. Crear los lineItems para Stripe
        for (ItemCarritoDto item : carritoDto.getItems()) {
            logger.info("Añadiendo lineItem a Stripe: {} - Precio: {} € - Cantidad: {}", item.getNombre(), item.getPrecio(), item.getCantidad());
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getCantidad())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) (item.getPrecio() * 100))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(item.getNombre())
                                                .setDescription(item.getSubtitulo())
                                                .build()
                                )
                                .build()
                    )
                    .build();

            lineItems.add(lineItem);
        }

        // Serializar carrito y cliente a JSON para pasarlo como metadata
        String carritoJson = objectMapper.writeValueAsString(carritoDto);
        String clienteJson = objectMapper.writeValueAsString(clienteDto);

        logger.info("Metadata carrito: {}", carritoJson);
        logger.info("Metadata cliente: {}", clienteJson);

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .putMetadata("cliente", clienteJson)
                .putMetadata("carrito", carritoJson)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        logger.info("Creando sesión en Stripe...");
        Session session = Session.create(params);
        logger.info("Sesión Stripe creada. URL: {}", session.getUrl());

        return session.getUrl();
    }
    
    
    public void procesarSesionStripe(ClienteDto clienteDto, CarritoDto carritoDto) {

        // Crear o actualizar cliente
        Cliente cliente = clienteService.crearActualizarCliente(clienteDto);

        // Calcular total
        BigDecimal total = carritoDto.getItems().stream()
                .map(i -> BigDecimal.valueOf(i.getPrecio())
                        .multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Crear compra total
        Compra compraTotal = new Compra();
        compraTotal.setCliente(cliente);
        compraTotal.setCodigoCompra(UUID.randomUUID().toString());
        compraTotal.setFechaCompra(LocalDate.now());
        compraTotal.setTotal(total);

        compraDao.save(compraTotal);
        logger.info("Compra registrada con ID: {}", compraTotal.getId());

        // Enviar email de confirmación general
        emailService.enviarConfirmacionCompraTotal(cliente.getEmail(), cliente.getNombre(), compraTotal);
        logger.info("Email de confirmación enviado a {}", cliente.getEmail());

        // Procesar items del carrito
        for (ItemCarritoDto item : carritoDto.getItems()) {
            try {
                switch (item.getTipo().toUpperCase()) {
                    case "CURSO":
                        procesarCurso(cliente, compraTotal, item);
                        break;
                    case "PRODUCTO":
                    	procesarProducto(cliente, compraTotal, item);
                        break;
                    case "TARJETA_REGALO":
                        logger.info("Tarjeta regalo generada para cliente {}", cliente.getEmail());
                        break;
                    case "SECRETO":
                    	procesarSecreto(cliente, compraTotal, item);
                        break;
                    default:
                        logger.warn("Tipo de item desconocido en el carrito: {}", item.getTipo());
                }
            } catch (Exception e) {
                logger.error("Error procesando item del carrito: {}", item, e);
            }
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
        logger.info("Compra de curso registrada: {} plazas para cliente {}", item.getCantidad(), cliente.getEmail());

        cursoFecha.setPlazasDisponibles(cursoFecha.getPlazasDisponibles() - item.getCantidad());
        cursoFechaDao.save(cursoFecha);
        logger.info("Plazas actualizadas para curso {}: ahora quedan {} plazas disponibles", cursoFecha.getCurso().getNombre(), cursoFecha.getPlazasDisponibles());

        // Enviar email confirmación curso
        emailService.enviarConfirmacionCursoIndividual(
                cliente.getEmail(),
                cliente.getNombre(),
                cursoFecha.getCurso().getNombre(),
                cursoFecha.getFecha().toString(),
                item.getCantidad(),
                cursoFecha.getCurso().getPrecio(),
                cursoFecha.getCurso().getInformacionExtra()
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
		logger.info("Compra de producto registrada: {} unidades para cliente {}", item.getCantidad(), cliente.getEmail());
		
		producto.setStock(producto.getStock() - item.getCantidad());
		logger.info("Actualizando stock del producto {}: ahora quedan {} unidades", producto.getNombre(), producto.getStock());
		
		// Enviar email confirmación producto
		emailService.enviarConfirmacionProductoIndividual(
			    cliente.getEmail(),
			    cliente.getNombre(),
			    producto.getNombre(),
			    item.getCantidad(),
			    producto.getPrecio()
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
		logger.info("Compra de secreto registrada para cliente {}", cliente.getEmail());
		
		// Enviar email confirmación secreto
	    emailService.enviarInformacionSecretoIndividual(
	            cliente.getEmail(),
	            cliente.getNombre(),
	            secreto.getNombre(),
	            secreto.getPdf()
	    );
		

	}
    
    
    
    
    
}
