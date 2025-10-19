package com.ashyaart.ashya_art_backend.filter;

import java.time.Instant;

public class LogErrorFilter {

    private Instant fechaCreacionDesde;
    private Instant fechaCreacionHasta;
    private String entorno;
    private String servidor;
    private String metodoHttp;
    private String rutaPeticion;
    private String claseError;
    private String loggerOrigen;

    // ======= GETTERS =======

    public Instant getFechaCreacionDesde() {
        return fechaCreacionDesde;
    }

    public Instant getFechaCreacionHasta() {
        return fechaCreacionHasta;
    }

    public String getEntorno() {
        return entorno;
    }

    public String getServidor() {
        return servidor;
    }

    public String getMetodoHttp() {
        return metodoHttp;
    }

    public String getRutaPeticion() {
        return rutaPeticion;
    }

    public String getClaseError() {
        return claseError;
    }

    public String getLoggerOrigen() {
        return loggerOrigen;
    }

    // ======= SETTERS =======

    public void setFechaCreacionDesde(Instant fechaCreacionDesde) {
        this.fechaCreacionDesde = fechaCreacionDesde;
    }

    public void setFechaCreacionHasta(Instant fechaCreacionHasta) {
        this.fechaCreacionHasta = fechaCreacionHasta;
    }

    public void setEntorno(String entorno) {
        this.entorno = entorno;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public void setMetodoHttp(String metodoHttp) {
        this.metodoHttp = metodoHttp;
    }

    public void setRutaPeticion(String rutaPeticion) {
        this.rutaPeticion = rutaPeticion;
    }

    public void setClaseError(String claseError) {
        this.claseError = claseError;
    }

    public void setLoggerOrigen(String loggerOrigen) {
        this.loggerOrigen = loggerOrigen;
    }
}
