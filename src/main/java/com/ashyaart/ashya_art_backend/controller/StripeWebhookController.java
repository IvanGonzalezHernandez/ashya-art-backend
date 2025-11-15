package com.ashyaart.ashya_art_backend.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.model.EventDataObjectDeserializer;
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

        /* ---------------------------
           VALIDACIÓN DE LA FIRMA
           --------------------------- */
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            logger.warn("Firma no válida en webhook Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma no válida");
        }

        logger.info("Evento Stripe recibido: {}", event.getType());

        /* ---------------------------
           SOLO PROCESA checkout.session.completed
           --------------------------- */
        if (!"checkout.session.completed".equals(event.getType())) {
            logger.info("Evento ignorado: {}", event.getType());
            return ResponseEntity.ok("Ignorado");
        }

        try {
            /* ---------------------------
               EXTRAER SESSION DE FORMA SEGURA
               --------------------------- */
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            Optional<StripeObject> obj = deserializer.getObject();

            if (obj.isEmpty() || !(obj.get() instanceof Session session)) {
                logger.warn("Evento checkout.session.completed sin Session válida");
                return ResponseEntity.ok("Evento sin Session");
            }

            /* ---------------------------
               VALIDACIÓN PAGO
               --------------------------- */
            if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                logger.warn("Session {} con payment_status={} (no se procesa)",
                        session.getId(), session.getPaymentStatus());
                return ResponseEntity.ok("No pagado");
            }

            /* ---------------------------
               OBTENER REF (ID DEL CARRITO)
               --------------------------- */
            String ref = session.getClientReferenceId();
            if (ref == null || ref.isBlank()) {
                logger.error("client_reference_id ausente en la sesión {}", session.getId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("client_reference_id ausente");
            }

            /* ---------------------------
               IDEMPOTENCIA - CARGAR CARRITO
               --------------------------- */
            Optional<Carrito> opt = carritoDao.findByIdAndConsumidoFalse(ref);
            if (opt.isEmpty()) {
                logger.info("Carrito {} ya consumido o no existe (idempotencia)", ref);
                return ResponseEntity.ok("Ya procesado");
            }

            Carrito ctx = opt.get();

            /* ---------------------------
               RECONSTRUIR DATOS DEL CLIENTE Y PEDIDO
               --------------------------- */
            ClienteDto clienteDto = objectMapper.readValue(ctx.getClienteJson(), ClienteDto.class);
            CarritoDto carritoDto = objectMapper.readValue(ctx.getCarritoJson(), CarritoDto.class);

            /* ---------------------------
               PROCESAR PAGO
               --------------------------- */
            stripeService.procesarSesionStripe(clienteDto, carritoDto);

            /* ---------------------------
               TARJETA REGALO (OPCIONAL)
               --------------------------- */
            String codigoTarjeta = session.getMetadata() != null
                    ? session.getMetadata().get("codigoTarjeta")
                    : null;

            if (codigoTarjeta != null && !codigoTarjeta.isBlank()) {
                tarjetaRegaloCompraDao.marcarTarjetaRegaloComoUsada(codigoTarjeta.trim().toUpperCase());
            }

            /* ---------------------------
               MARCAR CARRITO COMO CONSUMIDO
               --------------------------- */
            ctx.setConsumido(true);
            carritoDao.save(ctx);

            return ResponseEntity.ok("OK");

        } catch (Exception e) {

            /* ---------------------------
               ROLLBACK POR SI HAY FALLO
               --------------------------- */
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            logger.error("Error procesando sesión Stripe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando webhook");
        }
    }
}
