package com.ashyaart.ashya_art_backend.model;

import java.time.LocalDate;

public class TarjetaRegaloCompraEdicionDto {

    private Boolean canjeada;
    private LocalDate fechaBaja;

    public Boolean getCanjeada() { return canjeada; }
    public void setCanjeada(Boolean canjeada) { this.canjeada = canjeada; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }
}
