package com.ashyaart.ashya_art_backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.entity.Carrito;
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.repository.CarritoDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.service.StripeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Autowired private StripeService stripeService;
    @Autowired private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;
    @Autowired private CarritoDao carritoDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/stripe/webhook")
    @Transactional
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        logger.info("Webhook recibido");

        String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        Event event;

        // 1) Validar firma
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            logger.warn("Firma no válida en webhook Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        logger.info("Evento Stripe recibido: {}", event.getType());

        // 2) Solo procesamos checkout.session.completed
        if (!"checkout.session.completed".equals(event.getType())) {
            logger.info("Evento Stripe ignorado: {}", event.getType());
            return ResponseEntity.ok("Ignorado");
        }

        try {
            // 3) Intentar deserializar la Session
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            Optional<StripeObject> obj = deserializer.getObject();

            Session session;

            if (obj.isPresent() && obj.get() instanceof Session s) {
                // Caso normal: stripe-java sabe convertirlo
                session = s;
            } else {
                // Fallback: stripe-java no puede, tiramos del JSON crudo
                String rawJson = deserializer.getRawJson();
                logger.warn("No se pudo deserializar a Session, usando rawJson: {}", rawJson);

                JsonNode node = objectMapper.readTree(rawJson);

                session = new Session();
                session.setId(node.path("id").asText(null));
                session.setClientReferenceId(node.path("client_reference_id").asText(null));
                session.setPaymentStatus(node.path("payment_status").asText(null));

                if (node.has("metadata") && !node.get("metadata").isNull()) {
                    Map<String, String> metadata = new HashMap<>();
                    node.get("metadata").fields().forEachRemaining(e ->
                        metadata.put(e.getKey(), e.getValue().asText())
                    );
                    session.setMetadata(metadata);
                }
            }

            // 4) Verificar que está pagada
            if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                logger.warn("Session {} con payment_status={} (se ignora)",
                        session.getId(), session.getPaymentStatus());
                return ResponseEntity.ok("Ignorado: no pagado");
            }

            // 5) Recuperar client_reference_id (checkoutRef)
            String ref = session.getClientReferenceId();
            if (ref == null || ref.isBlank()) {
                logger.error("client_reference_id ausente en la sesión {}", session.getId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("client_reference_id ausente");
            }

            // 6) Buscar carrito idempotente
            Optional<Carrito> opt = carritoDao.findByIdAndConsumidoFalse(ref);
            if (opt.isEmpty()) {
                logger.info("Carrito {} ya consumido o no existe (idempotencia)", ref);
                return ResponseEntity.ok("Ya procesado");
            }

            Carrito ctx = opt.get();

            // 7) Reconstruir DTOs desde la BD
            ClienteDto clienteDto = objectMapper.readValue(ctx.getClienteJson(), ClienteDto.class);
            CarritoDto carritoDto = objectMapper.readValue(ctx.getCarritoJson(), CarritoDto.class);

            // 8) Procesar compra
            stripeService.procesarSesionStripe(clienteDto, carritoDto);

            // 9) Tarjeta regalo opcional
            String codigoTarjeta = session.getMetadata() != null
                    ? session.getMetadata().get("codigoTarjeta")
                    : null;
            if (codigoTarjeta != null && !codigoTarjeta.isBlank()) {
                tarjetaRegaloCompraDao.marcarTarjetaRegaloComoUsada(codigoTarjeta.trim().toUpperCase());
            }

            // 10) Marcar carrito como consumido (idempotencia)
            ctx.setConsumido(true);
            carritoDao.save(ctx);

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            logger.error("Error procesando sesión Stripe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar sesión Stripe");
        }
    }
}
