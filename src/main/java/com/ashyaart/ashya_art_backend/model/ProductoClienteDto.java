package com.ashyaart.ashya_art_backend.model;

import java.math.BigDecimal;

public class ProductoClienteDto {

    private String producto;
    private Integer cantidad;
    private BigDecimal precio;
    private String fechaCompra;
    private String metodoEnvio;
    private String numeroSeguimiento;

    public String getProducto() {
        return producto;
    }
    public void setProducto(String producto) {
        this.producto = producto;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getFechaCompra() {
        return fechaCompra;
    }
    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
    public String getMetodoEnvio() {
        return metodoEnvio;
    }
    public void setMetodoEnvio(String metodoEnvio) {
        this.metodoEnvio = metodoEnvio;
    }
    public String getNumeroSeguimiento() {
        return numeroSeguimiento;
    }
    public void setNumeroSeguimiento(String numeroSeguimiento) {
        this.numeroSeguimiento = numeroSeguimiento;
    }
}
