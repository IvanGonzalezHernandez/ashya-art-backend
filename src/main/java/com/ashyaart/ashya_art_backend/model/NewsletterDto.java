package com.ashyaart.ashya_art_backend.model;

import java.time.LocalDate;

public class NewsletterDto {

    private Long id;
    private String email;
    private LocalDate fechaRegistro;
    private LocalDate fechaBaja;
    private Boolean estado;

    public NewsletterDto() {
    }

    public NewsletterDto(Long id, String email, LocalDate fechaRegistro, LocalDate fechaBaja, Boolean estado) {
        this.id = id;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.fechaBaja = fechaBaja;
        this.estado = estado;
    }

    // Getters y setters

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
