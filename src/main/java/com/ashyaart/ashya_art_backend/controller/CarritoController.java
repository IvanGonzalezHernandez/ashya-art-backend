package com.ashyaart.ashya_art_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.model.CarritoClienteDto;
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.service.StripeService;

@RestController
@RequestMapping("api/carrito")
public class CarritoController {

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
        try {
            String url = stripeService.crearSesion(carritoClienteDto, successUrl, cancelUrl);
            return ResponseEntity.ok().body(new UrlResponse(url));
        } catch (Exception e) {
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