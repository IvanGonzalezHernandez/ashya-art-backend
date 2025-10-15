package com.ashyaart.ashya_art_backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "LOG_ERRORES")
public class LogError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Lob
    @Column(name = "mensaje_error", nullable = false)
    private String mensajeError;

    @Column(name = "clase_error")
    private String claseError;

    @Column(name = "metodo_error")
    private String metodoError;

    @Column(name = "linea_error")
    private Integer lineaError;

    @Column(name = "logger_origen")
    private String loggerOrigen;

    @Column(name = "metodo_http")
    private String metodoHttp;

    @Column(name = "ruta_peticion")
    private String rutaPeticion;

    @Column(name = "entorno")
    private String entorno;

    @Column(name = "servidor")
    private String servidor;

    @Column(name = "hash_traza", length = 40)
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
