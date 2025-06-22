package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "CURSO_FECHA")
public class CursoFecha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate fecha;

    private Integer plazasDisponibles;

    private LocalTime horaInicio;

    private LocalTime horaFin;
    
    @ManyToOne
    @JoinColumn(name = "ID_CURSO", nullable = false)
    private Curso curso;

    public CursoFecha() {}

    public CursoFecha(Curso curso, LocalDate fecha, Integer plazasDisponibles, LocalTime horaInicio, LocalTime horaFin) {
        this.curso = curso;
        this.fecha = fecha;
        this.plazasDisponibles = plazasDisponibles;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(Integer plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
