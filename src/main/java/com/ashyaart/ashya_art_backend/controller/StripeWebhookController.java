package com.ashyaart.ashya_art_backend.controller;

import java.io.IOException;

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
	
	@Autowired
	private ClienteDao clienteDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload, 
            @RequestHeader("Stripe-Signature") String sigHeader) {
    	System.out.println("✅ Webhook recibido: " + payload); // <--- este log

        String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET");
        Event event;

        try {
            // Validar que el webhook viene de Stripe
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("⚠️  Firma del webhook no válida.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                try {
                    // Deserializar los objetos
                    String clienteJson = session.getMetadata().get("cliente");
                    String carritoJson = session.getMetadata().get("carrito");

                    ClienteDto clienteDto = objectMapper.readValue(clienteJson, ClienteDto.class);
                    CarritoDto carrito = objectMapper.readValue(carritoJson, CarritoDto.class);
                    
                    Cliente cliente = ClienteAssembler.toEntity(clienteDto);
                    clienteDao.save(cliente);
                   
                   
                    // TODO: llama aquí tus métodos para guardar en BBDD o enviar emails

                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al deserializar metadata");
                }
            }
        }

        return ResponseEntity.ok("");
    }
}
