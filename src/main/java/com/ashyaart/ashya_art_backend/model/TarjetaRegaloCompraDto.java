package com.ashyaart.ashya_art_backend.model;

import java.time.LocalDate;

public class TarjetaRegaloCompraDto {

    private Long id;
    private String codigo;
    private String destinatario;

    private Boolean canjeada;
    private LocalDate fechaCompra;
    private LocalDate fechaCaducidad;
    private LocalDate fechaBaja;

    private Boolean estado;

    // Relaciones por ID (resueltas en el service)
    private Long idTarjeta;
    private Long idCliente;
    private Long idCompra;

    public TarjetaRegaloCompraDto() {}

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public Boolean getCanjeada() { return canjeada; }
    public void setCanjeada(Boolean canjeada) { this.canjeada = canjeada; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(LocalDate fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Long getIdTarjeta() { return idTarjeta; }
    public void setIdTarjeta(Long idTarjeta) { this.idTarjeta = idTarjeta; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Long getIdCompra() { return idCompra; }
    public void setIdCompra(Long idCompra) { this.idCompra = idCompra; }
}
