package com.ashyaart.ashya_art_backend.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ashyaart.ashya_art_backend.event.CompraEventos.ProductoCompradoEvent;
import com.ashyaart.ashya_art_backend.service.FrontendRebuildService;

/** Una compra de producto cambia su stock, que aparece en la página prerenderizada del producto. */
@Component
public class FrontendRebuildListener {

    private final FrontendRebuildService frontendRebuildService;

    public FrontendRebuildListener(FrontendRebuildService frontendRebuildService) {
        this.frontendRebuildService = frontendRebuildService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductoComprado(ProductoCompradoEvent event) {
        frontendRebuildService.solicitarRebuild("compra de producto");
    }
}
