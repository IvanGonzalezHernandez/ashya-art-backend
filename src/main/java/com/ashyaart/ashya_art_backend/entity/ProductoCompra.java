package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRODUCTO_COMPRA")
public class ProductoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "FECHA_COMPRA", nullable = false)
    private LocalDateTime fechaCompra;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PRODUCTO", nullable = false)
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    public ProductoCompra() {}

    public ProductoCompra(Cliente cliente, Producto producto, Integer cantidad, LocalDateTime fechaCompra) {
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fechaCompra = fechaCompra;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
    
    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }
}
