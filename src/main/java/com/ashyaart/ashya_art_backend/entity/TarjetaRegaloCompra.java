package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TARJETA_REGALO_COMPRA")
public class TarjetaRegaloCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private boolean canjeada = false;

    @Column(name = "FECHA_COMPRA", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "FECHA_CADUCIDAD", nullable = false)
    private LocalDate fechaCaducidad;
    
    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Boolean estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TARJETA", nullable = false)
    private TarjetaRegalo tarjetaRegalo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    public TarjetaRegaloCompra() {}

    public TarjetaRegaloCompra(String codigo, TarjetaRegalo tarjetaRegalo, Cliente cliente, boolean canjeada, LocalDate fechaCompra, LocalDate fechaCaducidad, boolean estado, LocalDate fechaBaja) {
        this.codigo = codigo;
        this.tarjetaRegalo = tarjetaRegalo;
        this.cliente = cliente;
        this.canjeada = canjeada;
        this.fechaCompra = fechaCompra;
        this.fechaCaducidad = fechaCaducidad;
        this.estado = estado;
        this.fechaBaja = fechaBaja;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TarjetaRegalo getTarjetaRegalo() {
        return tarjetaRegalo;
    }

    public void setTarjetaRegalo(TarjetaRegalo tarjetaRegalo) {
        this.tarjetaRegalo = tarjetaRegalo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isCanjeada() {
        return canjeada;
    }

    public void setCanjeada(boolean canjeada) {
        this.canjeada = canjeada;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }
    
    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }
}
