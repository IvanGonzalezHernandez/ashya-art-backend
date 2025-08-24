package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "CURSO_COMPRA")
public class CursoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    @Column(name = "PLAZAS_RESERVADAS")
    private Integer plazasReservadas;

    @NotNull
    @Column(name = "FECHA_RESERVA")
    private LocalDateTime fechaReserva;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FECHA", nullable = false)
    private CursoFecha cursoFecha;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    public CursoCompra() {}

    public CursoCompra(CursoFecha cursoFecha, Cliente cliente, Integer plazasReservadas, LocalDateTime fechaReserva) {
        this.cursoFecha = cursoFecha;
        this.cliente = cliente;
        this.plazasReservadas = plazasReservadas;
        this.fechaReserva = fechaReserva;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public CursoFecha getCursoFecha() {
        return cursoFecha;
    }

    public void setCursoFecha(CursoFecha cursoFecha) {
        this.cursoFecha = cursoFecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getPlazasReservadas() {
        return plazasReservadas;
    }

    public void setPlazasReservadas(Integer plazasReservadas) {
        this.plazasReservadas = plazasReservadas;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
    
    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }
}
