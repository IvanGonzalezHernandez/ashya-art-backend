package com.ashyaart.ashya_art_backend.controller;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
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

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            logger.warn("Firma no válida en webhook Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                // Obtener la Session del propio evento (evita otra llamada si es posible)
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new IllegalStateException("No hay Session en el evento"));

                // 1) Verificar que está pagada
                if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                    logger.warn("Session {} con payment_status={} (se ignora)",
                            session.getId(), session.getPaymentStatus());
                    return ResponseEntity.ok("Ignorado: no pagado");
                }

                // 2) Recuperar el ref que generaste en crearSesion
                String ref = session.getClientReferenceId();
                if (ref == null || ref.isBlank()) {
                    logger.error("client_reference_id ausente en la sesión {}", session.getId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("client_reference_id ausente");
                }

                // 3) Cargar el contexto del carrito desde la BD (y asegurar idempotencia)
                Optional<Carrito> opt = carritoDao.findByIdAndConsumidoFalse(ref);
                if (opt.isEmpty()) {
                    logger.info("Carrito {} no encontrado o ya consumido (idempotencia)", ref);
                    return ResponseEntity.ok("Ya procesado o no existe");
                }

                Carrito ctx = opt.get();

                // 4) Reconstruir DTOs desde tu BD (no desde metadata)
                ClienteDto clienteDto = objectMapper.readValue(ctx.getClienteJson(), ClienteDto.class);
                CarritoDto carritoDto = objectMapper.readValue(ctx.getCarritoJson(), CarritoDto.class);

                // 5) Procesar la compra completa
                stripeService.procesarSesionStripe(clienteDto, carritoDto);

                // 6) Marcar tarjeta regalo como usada si venía en metadata (opcional)
                String codigoTarjeta = session.getMetadata() != null
                        ? session.getMetadata().get("codigoTarjeta")
                        : null;
                if (codigoTarjeta != null && !codigoTarjeta.isBlank()) {
                    tarjetaRegaloCompraDao.marcarTarjetaRegaloComoUsada(codigoTarjeta.trim().toUpperCase());
                }

                // 7) Idempotencia: marcar consumido
                ctx.setConsumido(true);
                carritoDao.save(ctx);

            } catch (Exception e) {
                logger.error("Error procesando sesión Stripe", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al procesar sesión Stripe");
            }
        }

        return ResponseEntity.ok("Webhook recibido");
    }
}
