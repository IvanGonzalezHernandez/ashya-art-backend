package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "NEWSLETTER")
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Boolean estado;

    public Newsletter() {
        this.fechaRegistro = LocalDate.now();
        this.estado = true;
    }

    public Newsletter(String email) {
        this.email = email;
        this.fechaRegistro = LocalDate.now();
        this.estado = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
