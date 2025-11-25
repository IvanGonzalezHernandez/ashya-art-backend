package com.ashyaart.ashya_art_backend.event;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ashyaart.ashya_art_backend.entity.Compra;
import com.ashyaart.ashya_art_backend.model.CarritoDto;
import com.ashyaart.ashya_art_backend.model.ClienteDto;

public class CompraEventos {

    public record CompraTotalConfirmadaEvent(
            String email,
            String nombreCliente,
            Compra compra
    ) {}

    public record CursoCompradoEvent(
            String email,
            String nombreCliente,
            String nombreCurso,
            LocalDate fecha,
            String horaInicio,
            int plazas,
            BigDecimal precio,
            String informacionExtra
    ) {}

    public record ProductoCompradoEvent(
            String email,
            String nombreCliente,
            String nombreProducto,
            int cantidad,
            BigDecimal precioUnitario
    ) {}

    public record SecretoCompradoEvent(
    	    String email,
    	    String nombreCliente,
    	    String nombreSecreto,
    	    byte[] pdfBytes
    ) {}

    public record TarjetaRegaloCompradaEvent(
            String email,
            String nombreCliente,
            String destinatario,
            String codigo,
            BigDecimal importe,
            LocalDate fechaCaducidad
    ) {}
    
    public record CompraStripeAdminSuccessEvent(
            String emailCliente,
            String nombreCliente,
            Compra compra,
            CarritoDto carrito
    ) {}

    public record CompraStripeAdminErrorEvent(
            String emailCliente,
            String nombreCliente,
            String motivo,
            CarritoDto carrito
    ) {}
    
    public record CompraNoStripeAdminEvent(
            String flujo,
            boolean exito,
            String mensajeError,
            ClienteDto cliente,
            CarritoDto carrito,
            Compra compra
    ) {}

}
