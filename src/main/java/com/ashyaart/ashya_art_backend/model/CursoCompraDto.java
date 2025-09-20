package com.ashyaart.ashya_art_backend.model;

import java.time.LocalDateTime;

public class CursoCompraDto {

    private Long id;
    private Long idFecha;
    private Long idCliente;
    private Integer plazasReservadas;
    private LocalDateTime fechaReserva;
    private String nombreCliente;
    private String telefono;
    private String email;
    private String nombreCurso;
    private String fechaCurso;

    public CursoCompraDto() {}

    public CursoCompraDto(Long id, Long idFecha, Long idCliente, Integer plazasReservadas, LocalDateTime fechaReserva) {
        this.id = id;
        this.idFecha = idFecha;
        this.idCliente = idCliente;
        this.plazasReservadas = plazasReservadas;
        this.fechaReserva = fechaReserva;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdFecha() {
        return idFecha;
    }

    public void setIdFecha(Long idFecha) {
        this.idFecha = idFecha;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
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
    
	public String getNombreCliente() {
		return nombreCliente;
	}
	
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	
	public String getNombreCurso() {
		return nombreCurso;
	}
	
	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}
	
	public String getFechaCurso() {
		return fechaCurso;
	}
	
	public void setFechaCurso(String fechaCurso) {
		this.fechaCurso = fechaCurso;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
