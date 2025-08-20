package com.ashyaart.ashya_art_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.model.CarritoClienteDto;
import com.ashyaart.ashya_art_backend.service.StripeService;

@RestController
@RequestMapping("api/carrito")
public class CarritoController {
	
	private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    private final StripeService stripeService;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public CarritoController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public ResponseEntity<?> crearSesionStripe(@RequestBody CarritoClienteDto carritoClienteDto) {
        logger.info("📥 Request recibido para crear sesión Stripe");
        logger.info("🧑 Cliente: {}", carritoClienteDto.getCliente());
        logger.info("📦 Carrito: {}", carritoClienteDto.getCarrito());

        try {
            String url = stripeService.crearSesion(carritoClienteDto, successUrl, cancelUrl);
            logger.info("✅ Sesión Stripe creada correctamente. URL: {}", url);
            return ResponseEntity.ok().body(new UrlResponse(url));
        } catch (Exception e) {
            logger.error("❌ Error creando sesión Stripe", e);
            return ResponseEntity.status(500).body("Error creando sesión Stripe: " + e.getMessage());
        }
    }


    static class UrlResponse {
        private String url;
        public UrlResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}