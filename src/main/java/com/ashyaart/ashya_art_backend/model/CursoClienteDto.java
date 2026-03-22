package com.ashyaart.ashya_art_backend.model;

public class CursoClienteDto {
	
    private String curso;
    private String fechaCurso;
    private String fechaReserva;
    private Integer plazasReservadas;
    
	public String getCurso() {
		return curso;
	}
	public void setCurso(String curso) {
		this.curso = curso;
	}
	public String getFechaCurso() {
		return fechaCurso;
	}
	public void setFechaCurso(String fechaCurso) {
		this.fechaCurso = fechaCurso;
	}
	public String getFechaReserva() {
		return fechaReserva;
	}
	public void setFechaReserva(String fechaReserva) {
		this.fechaReserva = fechaReserva;
	}
	public Integer getPlazasReservadas() {
		return plazasReservadas;
	}
	public void setPlazasReservadas(Integer plazasReservadas) {
		this.plazasReservadas = plazasReservadas;
	}
    
    

}
