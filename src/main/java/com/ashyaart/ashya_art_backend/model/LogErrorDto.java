package com.ashyaart.ashya_art_backend.model;

import java.time.Instant;

public class LogErrorDto {

    private Long id;
    private Instant fechaCreacion;
    private String mensajeError;
    private String claseError;
    private String metodoError;
    private Integer lineaError;
    private String loggerOrigen;
    private String metodoHttp;
    private String rutaPeticion;
    private String entorno;
    private String servidor;
    private String hashTraza;

    // ======= GETTERS =======

    public Long getId() {
        return id;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public String getClaseError() {
        return claseError;
    }

    public String getMetodoError() {
        return metodoError;
    }

    public Integer getLineaError() {
        return lineaError;
    }

    public String getLoggerOrigen() {
        return loggerOrigen;
    }

    public String getMetodoHttp() {
        return metodoHttp;
    }

    public String getRutaPeticion() {
        return rutaPeticion;
    }

    public String getEntorno() {
        return entorno;
    }

    public String getServidor() {
        return servidor;
    }

    public String getHashTraza() {
        return hashTraza;
    }

    // ======= SETTERS =======

    public void setId(Long id) {
        this.id = id;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public void setClaseError(String claseError) {
        this.claseError = claseError;
    }

    public void setMetodoError(String metodoError) {
        this.metodoError = metodoError;
    }

    public void setLineaError(Integer lineaError) {
        this.lineaError = lineaError;
    }

    public void setLoggerOrigen(String loggerOrigen) {
        this.loggerOrigen = loggerOrigen;
    }

    public void setMetodoHttp(String metodoHttp) {
        this.metodoHttp = metodoHttp;
    }

    public void setRutaPeticion(String rutaPeticion) {
        this.rutaPeticion = rutaPeticion;
    }

    public void setEntorno(String entorno) {
        this.entorno = entorno;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public void setHashTraza(String hashTraza) {
        this.hashTraza = hashTraza;
    }
}
