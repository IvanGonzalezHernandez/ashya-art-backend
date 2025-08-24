package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SECRETO_COMPRA")
public class SecretoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_secreto", nullable = false)
    private Secreto secreto;

    @ManyToOne
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Secreto getSecreto() { return secreto; }
    public void setSecreto(Secreto secreto) { this.secreto = secreto; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }
}
