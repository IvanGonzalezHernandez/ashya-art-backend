package com.ashyaart.ashya_art_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.CarritoClienteDto;
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Autowired
    private StockService stockService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StripeService() {
        Stripe.apiKey = System.getenv("STRIPE_TEST_KEY");
        logger.info("Stripe API Key configurada");
    }

    public String crearSesion(CarritoClienteDto carritoClienteDto, String successUrl, String cancelUrl) throws Exception {
        // Extraer carrito y cliente
        CarritoDto carritoDto = carritoClienteDto.getCarrito();
        ClienteDto clienteDto = carritoClienteDto.getCliente();

        logger.info("Creando sesión Stripe para cliente: {}", clienteDto.getEmail());
        logger.info("Carrito contiene {} items", carritoDto.getItems().size());

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // 1. Validar stock/plazas
        for (ItemCarritoDto item : carritoDto.getItems()) {
            logger.info("Validando stock para item: {} - Cantidad: {}", item.getNombre(), item.getCantidad());
            if (!stockService.hayStockSuficiente(item)) {
                logger.warn("No hay suficiente stock o plazas para el item: {}", item.getNombre());
                throw new IllegalArgumentException(
                    "No hay suficiente stock o plazas para el item: " + item.getNombre()
                );
            }
        }

        // 2. Crear los lineItems para Stripe
        for (ItemCarritoDto item : carritoDto.getItems()) {
            logger.info("Añadiendo lineItem a Stripe: {} - Precio: {} € - Cantidad: {}", item.getNombre(), item.getPrecio(), item.getCantidad());
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getCantidad())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount((long) (item.getPrecio() * 100))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(item.getNombre())
                                                .setDescription(item.getSubtitulo())
                                                .build()
                                )
                                .build()
                    )
                    .build();

            lineItems.add(lineItem);
        }

        // Serializar carrito y cliente a JSON para pasarlo como metadata
        String carritoJson = objectMapper.writeValueAsString(carritoDto);
        String clienteJson = objectMapper.writeValueAsString(clienteDto);

        logger.info("Metadata carrito: {}", carritoJson);
        logger.info("Metadata cliente: {}", clienteJson);

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .putMetadata("cliente", clienteJson)
                .putMetadata("carrito", carritoJson)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        logger.info("Creando sesión en Stripe...");
        Session session = Session.create(params);
        logger.info("Sesión Stripe creada. URL: {}", session.getUrl());

        return session.getUrl();
    }
}
