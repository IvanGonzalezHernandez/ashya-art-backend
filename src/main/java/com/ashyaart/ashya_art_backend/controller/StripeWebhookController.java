package com.ashyaart.ashya_art_backend.controller;

import java.io.IOException;

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
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.repository.ClienteDao;
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

        // 1️⃣ Log del payload completo
        logger.info("✅ Webhook recibido: {}", payload);

        String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        Event event;

        try {
            // 2️⃣ Validación de la firma
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            logger.info("🔑 Firma verificada correctamente");
        } catch (SignatureVerificationException e) {
            logger.warn("⚠️ Firma del webhook no válida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        // 3️⃣ Procesar evento de pago completado
        if ("checkout.session.completed".equals(event.getType())) {
            logger.info("💰 Evento checkout.session.completed recibido");

            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                try {
                    // 4️⃣ Obtener metadata
                    String clienteJson = session.getMetadata().get("cliente");
                    String carritoJson = session.getMetadata().get("carrito");
                    logger.info("📦 Metadata recibida: cliente={}, carrito={}", clienteJson, carritoJson);

                    // 5️⃣ Deserializar a objetos Java
                    ClienteDto clienteDto = objectMapper.readValue(clienteJson, ClienteDto.class);
                    CarritoDto carritoDto = objectMapper.readValue(carritoJson, CarritoDto.class);
                    logger.info("🧑 Cliente deserializado: {}", clienteDto.getNombre());

                    // 6️⃣ Guardar cliente
                    Cliente cliente = ClienteAssembler.toEntity(clienteDto);
                    clienteDao.save(cliente);
                    logger.info("✅ Cliente guardado en DB: {}", cliente.getNombre());

                } catch (IOException e) {
                    logger.error("❌ Error al deserializar metadata", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al deserializar metadata");
                }
            } else {
                logger.warn("⚠️ Session deserializada es null");
            }
        } else {
            logger.info("ℹ️ Evento ignorado: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook recibido");
    }
}