package com.ashyaart.ashya_art_backend.model;

import java.time.LocalDate;

public class SecretoCompraDto {

    private Long id;
    private Long clienteId;
    private Long secretoId;
    private Long compraId;
    private LocalDate fechaCompra;

    // ======= GETTERS =======

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getSecretoId() {
        return secretoId;
    }

    public Long getCompraId() {
        return compraId;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    // ======= SETTERS =======

    public void setId(Long id) {
        this.id = id;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setSecretoId(Long secretoId) {
        this.secretoId = secretoId;
    }

    public void setCompraId(Long compraId) {
        this.compraId = compraId;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
