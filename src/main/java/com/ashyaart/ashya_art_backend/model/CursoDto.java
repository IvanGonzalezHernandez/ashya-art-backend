package com.ashyaart.ashya_art_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CursoDto {
    private Long id;
    private String nombre;
    private String subtitulo;
    private String descripcion;
    private BigDecimal precio;
    private String img;
    private Boolean estado;
    private LocalDate fechaBaja;

    public CursoDto() {}

    public CursoDto(Long id, String nombre, String subtitulo, String descripcion, BigDecimal precio, String img, Boolean estado, LocalDate fechaBaja) {
        this.id = id;
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.img = img;
        this.estado = estado;
        this.fechaBaja = fechaBaja;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }
}
