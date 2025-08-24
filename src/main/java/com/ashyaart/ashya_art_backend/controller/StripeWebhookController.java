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

import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.service.StripeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Autowired
    private StripeService stripeService;

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
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                // Obtener sessionId y session
                String rawJson = event.getDataObjectDeserializer().getRawJson();
                Session session = Session.retrieve(objectMapper.readTree(rawJson).get("id").asText());

                // Metadata cliente y carrito
                ClienteDto clienteDto = objectMapper.readValue(session.getMetadata().get("cliente"), ClienteDto.class);
                CarritoDto carritoDto = objectMapper.readValue(session.getMetadata().get("carrito"), CarritoDto.class);

                // Delegar todo el procesado al service
                stripeService.procesarSesionStripe(clienteDto, carritoDto);

            } catch (Exception e) {
                logger.error("Error procesando sesión Stripe", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar sesión Stripe");
            }
        }
        

        return ResponseEntity.ok("Webhook recibido");
    }

}
