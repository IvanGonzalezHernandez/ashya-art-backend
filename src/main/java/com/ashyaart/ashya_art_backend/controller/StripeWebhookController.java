package com.ashyaart.ashya_art_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.assembler.ClienteAssembler;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.repository.ClienteDao;
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
    private ClienteDao clienteDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        logger.info("📩 Webhook recibido");

        String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            logger.info("🔑 Firma verificada correctamente");
        } catch (SignatureVerificationException e) {
            logger.warn("⚠️ Firma no válida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            logger.info("💰 Pago completado");

            try {
                // Intentar deserializar directamente
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (session == null) {
                    // ⚡ Si viene como referencia, obtener solo el id del JSON
                    String rawJson = event.getDataObjectDeserializer().getRawJson();
                    if (rawJson == null) {
                        throw new RuntimeException("No se encontró JSON en el evento");
                    }

                    JsonNode node = objectMapper.readTree(rawJson);
                    String sessionId = node.get("id").asText();
                    session = Session.retrieve(sessionId);
                }

                String sessionId = session.getId();
                logger.info("🆔 Stripe Session ID: {}", sessionId);

                // Metadata del cliente
                String clienteJson = session.getMetadata().get("cliente");
                if (clienteJson == null || clienteJson.isEmpty()) {
                    logger.warn("⚠️ No se encontró metadata de cliente en sesión {}", sessionId);
                    return ResponseEntity.ok("Sin metadata de cliente");
                }

                ClienteDto clienteDto = objectMapper.readValue(clienteJson, ClienteDto.class);
                Cliente cliente = ClienteAssembler.toEntity(clienteDto);

                // Guardar cliente
                clienteDao.save(cliente);
                logger.info("✅ Cliente guardado en DB: {}", cliente.getNombre());

            } catch (Exception e) {
                logger.error("❌ Error procesando sesión Stripe", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al procesar sesión Stripe");
            }
        }

        return ResponseEntity.ok("Webhook recibido");
    }

}
