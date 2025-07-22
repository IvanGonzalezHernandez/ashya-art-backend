package com.ashyaart.ashya_art_backend.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class CursoFechaDto {

    private Long id;
    private LocalDate fecha;
    private Integer plazasDisponibles;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long idCurso;
    private String nombreCurso;

    public CursoFechaDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }
    
	public String getNombreCurso() {
		return nombreCurso;
	}
	
	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}
}
