package com.ashyaart.ashya_art_backend.controller;


import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.service.ClienteService;
import com.ashyaart.ashya_art_backend.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private CursoCompraDao cursoCompraDao;
    @Autowired
    private CursoFechaDao cursoFechaDao;
    @Autowired
    private EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        logger.info("Webhook recibido");

        String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            logger.info("Firma verificada correctamente");
        } catch (SignatureVerificationException e) {
            logger.warn("Firma no válida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            logger.info("Pago completado");

            try {
                // Intentar deserializar directamente
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (session == null) {
                    // Si viene como referencia, obtener solo el id del JSON
                    String rawJson = event.getDataObjectDeserializer().getRawJson();
                    if (rawJson == null) {
                        throw new RuntimeException("No se encontró JSON en el evento");
                    }

                    JsonNode node = objectMapper.readTree(rawJson);
                    String sessionId = node.get("id").asText();
                    session = Session.retrieve(sessionId);
                }

                String sessionId = session.getId();
                logger.info("Stripe Session ID: {}", sessionId);

                // Metadata del cliente
                String clienteJson = session.getMetadata().get("cliente");
                if (clienteJson == null || clienteJson.isEmpty()) {
                    logger.warn("No se encontró metadata de cliente en sesión {}", sessionId);
                    return ResponseEntity.ok("Sin metadata de cliente");
                }

                ClienteDto clienteDto = objectMapper.readValue(clienteJson, ClienteDto.class);

                // Crear o actualizar cliente usando el service
	            Cliente cliente = clienteService.crearActualizarCliente(clienteDto);
	        
	            
	            String itemsCarrito = session.getMetadata().get("carrito");
	            if (itemsCarrito == null || itemsCarrito.isEmpty()) {
	                logger.warn("No se encontró metadata de curso en sesión {}", sessionId);
	                return ResponseEntity.ok("Sin metadata de curso");
	            }

	            CarritoDto carritoDto;
	            try {
	                carritoDto = objectMapper.readValue(itemsCarrito, CarritoDto.class);
	                logger.info("Carrito recibido: {}", objectMapper.writeValueAsString(carritoDto.getItems()));
	            } catch (JsonProcessingException e) {
	                logger.error("Error deserializando items del carrito", e);
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                     .body("Error al procesar el carrito");
	            }

	            // Iterar sobre cada item
	            for (ItemCarritoDto item : carritoDto.getItems()) {
	                try {
	                    switch (item.getTipo().toUpperCase()) {
	                        case "CURSO":
	                            Long idCursoFecha = Long.valueOf(item.getId());
	                            CursoFecha cursoFecha = cursoFechaDao.findById(idCursoFecha)
	                                    .orElseThrow(() -> new RuntimeException("CursoFecha no encontrada: " + idCursoFecha));

	                            CursoCompra compra = new CursoCompra();
	                            compra.setCursoFecha(cursoFecha);
	                            compra.setCliente(cliente);
	                            compra.setPlazasReservadas(item.getCantidad());
	                            compra.setFechaReserva(LocalDateTime.now());

	                            cursoCompraDao.save(compra);
	                            logger.info("Compra de curso registrada: {} plazas para cliente {}", item.getCantidad(), cliente.getEmail());
	                            
	                            cursoFecha.setPlazasDisponibles(cursoFecha.getPlazasDisponibles() - item.getCantidad());
	                            cursoFechaDao.save(cursoFecha);
								logger.info("Plazas actualizadas para curso {}: ahora quedan {} plazas disponibles", cursoFecha.getCurso().getNombre(), cursoFecha.getPlazasDisponibles());
								
								// Enviar email individual
								emailService.enviarConfirmacionCursoIndividual(
								    cliente.getEmail(),
								    cliente.getNombre(),
								    cursoFecha.getCurso().getNombre(),
								    cursoFecha.getFecha().toString(),
								    item.getCantidad(),
								    cursoFecha.getCurso().getPrecio(),
								    cursoFecha.getCurso().getInformacionExtra()
								);
	                            break;

	                        case "PRODUCTO":
	                            logger.info("Compra de producto procesada: {} unidades", item.getCantidad());
	                            break;

	                        case "TARJETA_REGALO":
	                            logger.info("Tarjeta regalo generada para cliente {}", cliente.getEmail());
	                            break;

	                        case "SECRETO":
	                            logger.info("Secreto registrado para cliente {}", cliente.getEmail());
	                            break;

	                        default:
	                            logger.warn("Tipo de item desconocido en el carrito: {}", item.getTipo());
	                    }
	                } catch (Exception e) {
	                    logger.error("Error procesando item del carrito: {}", item, e);
	                }
	            }



	             
	             


            } catch (Exception e) {
                logger.error("Error procesando sesión Stripe", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al procesar sesión Stripe");
            }
        }

        return ResponseEntity.ok("Webhook recibido");
    }

}
