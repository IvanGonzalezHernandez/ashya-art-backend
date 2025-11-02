package com.ashyaart.ashya_art_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.model.CarritoClienteDto;
import com.ashyaart.ashya_art_backend.service.NoStripeService;
import com.ashyaart.ashya_art_backend.service.StripeService;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    private final StripeService stripeService;

    @Autowired
    private NoStripeService noStripeService;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public CarritoController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> crearSesionStripe(@RequestBody CarritoClienteDto carritoClienteDto) {
        logger.info("Request crear sesión Stripe. Email: {}",
                carritoClienteDto.getCliente() != null ? carritoClienteDto.getCliente().getEmail() : "desconocido");
        logger.debug("Cliente: {}", carritoClienteDto.getCliente());
        logger.debug("Carrito: {}", carritoClienteDto.getCarrito());

        try {
            String url = stripeService.crearSesion(carritoClienteDto, successUrl, cancelUrl);
            return ResponseEntity.ok(new UrlResponse(url));
        } catch (Exception e) {
            logger.error("Error creando sesión Stripe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando sesión Stripe: " + e.getMessage());
        }
    }

    @PostMapping(value = "/no-stripe", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> procesarCompraSinStripe(@RequestBody CarritoClienteDto dto) {
        try {
            String codigo = noStripeService.procesarCompraSinStripe(dto);
            return ResponseEntity.ok(new CompraResponse(codigo));
        } catch (Exception e) {
            logger.error("Error en compra sin Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    static class UrlResponse {
        private String url;
        public UrlResponse() {}
        public UrlResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    static class CompraResponse {
        private String codigo;
        public CompraResponse() {}
        public CompraResponse(String codigo) { this.codigo = codigo; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
    }
}
