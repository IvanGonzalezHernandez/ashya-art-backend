package com.ashyaart.ashya_art_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Contador agregado de páginas vistas en la web pública: una fila por día, página, origen,
 * dispositivo e idioma, con el número de vistas. No guarda IP, cookies ni nada que identifique
 * a la persona. Las vistas con origen INTERNAL son navegaciones dentro de la web; el resto
 * son la primera página de una visita (entradas).
 */
@Entity
@Table(name = "visita_diaria", uniqueConstraints = @UniqueConstraint(
        name = "uk_visita_diaria", columnNames = { "fecha", "ruta", "origen", "dispositivo", "idioma" }))
public class VisitaDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 200)
    private String ruta;

    @Column(nullable = false, length = 20)
    private String origen;

    @Column(nullable = false, length = 10)
    private String dispositivo;

    @Column(nullable = false, length = 5)
    private String idioma;

    @Column(nullable = false)
    private int visitas;

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getRuta() {
        return ruta;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public String getIdioma() {
        return idioma;
    }

    public int getVisitas() {
        return visitas;
    }
}
