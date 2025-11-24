package com.ashyaart.ashya_art_backend.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.ashyaart.ashya_art_backend.event.*;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraNoStripeAdminEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraStripeAdminErrorEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraStripeAdminSuccessEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraTotalConfirmadaEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CursoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.ProductoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.SecretoCompradoEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.TarjetaRegaloCompradaEvent;
import com.ashyaart.ashya_art_backend.service.EmailService;

import jakarta.mail.MessagingException;

@Component
public class EmailCompraListener {

    private final EmailService emailService;

    public EmailCompraListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompraTotalConfirmada(CompraTotalConfirmadaEvent event) throws MessagingException {
        emailService.enviarConfirmacionCompraTotal(
            event.email(),
            event.nombreCliente(),
            event.compra()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCursoComprado(CursoCompradoEvent event) throws MessagingException {
        emailService.enviarConfirmacionCursoIndividual(
            event.email(),
            event.nombreCliente(),
            event.nombreCurso(),
            event.fecha().toString(),
            event.horaInicio(),
            event.plazas(),
            event.precio(),
            event.informacionExtra()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductoComprado(ProductoCompradoEvent event) throws MessagingException {
        emailService.enviarConfirmacionProductoIndividual(
            event.email(),
            event.nombreCliente(),
            event.nombreProducto(),
            event.cantidad(),
            event.precioUnitario()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSecretoComprado(SecretoCompradoEvent event) throws MessagingException {
        emailService.enviarConfirmacionSecretoIndividual(
            event.email(),
            event.nombreCliente(),
            event.nombreSecreto(),
            event.pdfBytes()
        );
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTarjetaRegaloComprada(TarjetaRegaloCompradaEvent event) throws MessagingException {
        emailService.enviarConfirmacionTarjetaRegaloIndividual(
            event.email(),
            event.nombreCliente(),
            event.destinatario(),
            event.codigo(),
            event.importe(),
            event.fechaCaducidad()
        );
    }
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompraStripeAdminSuccess(CompraStripeAdminSuccessEvent event) {
        emailService.enviarNotificacionAdminCompraStripe(
                event.emailCliente(),
                event.nombreCliente(),
                event.compra()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onCompraStripeAdminError(CompraStripeAdminErrorEvent event) {
        emailService.enviarNotificacionAdminCompraStripeError(
                event.emailCliente(),
                event.nombreCliente(),
                event.motivo()
        );
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompraNoStripeAdminSuccess(CompraNoStripeAdminEvent event) throws MessagingException {
        if (!event.exito()) {
            return;
        }
        emailService.enviarNotificacionAdminCompraNoStripe(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onCompraNoStripeAdminError(CompraNoStripeAdminEvent event) throws MessagingException {
        if (event.exito()) {
            return;
        }
        emailService.enviarNotificacionAdminCompraNoStripe(event);
    }
}
