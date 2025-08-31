package com.ashyaart.ashya_art_backend.model;

public class FiringSolicitudDto {
	private String tipoServicio;
    private Integer numeroPiezas;
    private String nombre;
    private String detallesMaterial;
    private String email;
    private String telefono;
    private String preguntasAdicionales;

    // Getters y Setters
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public Integer getNumeroPiezas() { return numeroPiezas; }
    public void setNumeroPiezas(Integer numeroPiezas) { this.numeroPiezas = numeroPiezas; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDetallesMaterial() { return detallesMaterial; }
    public void setDetallesMaterial(String detallesMaterial) { this.detallesMaterial = detallesMaterial; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPreguntasAdicionales() { return preguntasAdicionales; }
    public void setPreguntasAdicionales(String preguntasAdicionales) { this.preguntasAdicionales = preguntasAdicionales; }

}
