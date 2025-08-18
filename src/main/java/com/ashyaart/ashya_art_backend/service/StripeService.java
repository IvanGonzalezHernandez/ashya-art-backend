package com.ashyaart.ashya_art_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    public StripeService() {
        Stripe.apiKey = "sk_test_51R1AfzQsK7W2R2yG8WVaLsvv1BRvqO4LKG8RAtZXhUYhgijhzjcETNftYFhFafv67fYfMTKJNkGEyMHRd2qxEajp00j2cVA5bx";
    }

    public String crearSesion(CarritoDto carritoDto, String successUrl, String cancelUrl) throws Exception {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (ItemCarritoDto item : carritoDto.getItems()) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getCantidad())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount((long) (item.getPrecio() * 100)) // Stripe usa centimos
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getNombre())
                                    .build()
                            )
                            .build()
                    )
                    .build();

            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addAllLineItem(lineItems)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
