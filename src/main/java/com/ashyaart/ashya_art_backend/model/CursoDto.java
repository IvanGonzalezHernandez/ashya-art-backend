package com.ashyaart.ashya_art_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursoDto {
    private Long id;
    private String nombre;
    private String subtitulo;
    private String descripcion;
    private String nivel;
    private String duracion;
    private String piezas;
    private String materiales;
    private String localizacion;
    private BigDecimal precio;

    // URLs de las imágenes (archivos en disco, servidos vía /uploads/**)
    private String img1Url;
    private String img2Url;
    private String img3Url;
    private String img4Url;
    private String img5Url;

    // Flags para borrar imágenes concretas en actualización
    private Boolean deleteImg1;
    private Boolean deleteImg2;
    private Boolean deleteImg3;
    private Boolean deleteImg4;
    private Boolean deleteImg5;

    private Boolean estado;
    private LocalDate fechaBaja;
    private Integer plazasMaximas;
    private Integer orden;
    private String informacionExtra;

    public CursoDto() {}

    public CursoDto(Long id, String nombre, String subtitulo, String descripcion, BigDecimal precio, Boolean estado,
            LocalDate fechaBaja, String nivel, String duracion, String piezas, String materiales, String localizacion,
            Integer plazasMaximas, String informacionExtra) {
        this.id = id;
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.fechaBaja = fechaBaja;
        this.nivel = nivel;
        this.duracion = duracion;
        this.piezas = piezas;
        this.materiales = materiales;
        this.localizacion = localizacion;
        this.plazasMaximas = plazasMaximas;
        this.informacionExtra = informacionExtra;
    }

    // --- Getters / Setters básicos ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getPiezas() { return piezas; }
    public void setPiezas(String piezas) { this.piezas = piezas; }

    public String getMateriales() { return materiales; }
    public void setMateriales(String materiales) { this.materiales = materiales; }

    public Integer getPlazasMaximas() { return plazasMaximas; }
    public void setPlazasMaximas(Integer plazasMaximas) { this.plazasMaximas = plazasMaximas; }

    public String getInformacionExtra() { return informacionExtra; }
    public void setInformacionExtra(String informacionExtra) { this.informacionExtra = informacionExtra; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImg1Url() { return img1Url; }
    public void setImg1Url(String img1Url) { this.img1Url = img1Url; }

    public String getImg2Url() { return img2Url; }
    public void setImg2Url(String img2Url) { this.img2Url = img2Url; }

    public String getImg3Url() { return img3Url; }
    public void setImg3Url(String img3Url) { this.img3Url = img3Url; }

    public String getImg4Url() { return img4Url; }
    public void setImg4Url(String img4Url) { this.img4Url = img4Url; }

    public String getImg5Url() { return img5Url; }
    public void setImg5Url(String img5Url) { this.img5Url = img5Url; }

    public Boolean getDeleteImg1() { return deleteImg1; }
    public void setDeleteImg1(Boolean deleteImg1) { this.deleteImg1 = deleteImg1; }

    public Boolean getDeleteImg2() { return deleteImg2; }
    public void setDeleteImg2(Boolean deleteImg2) { this.deleteImg2 = deleteImg2; }

    public Boolean getDeleteImg3() { return deleteImg3; }
    public void setDeleteImg3(Boolean deleteImg3) { this.deleteImg3 = deleteImg3; }

    public Boolean getDeleteImg4() { return deleteImg4; }
    public void setDeleteImg4(Boolean deleteImg4) { this.deleteImg4 = deleteImg4; }

    public Boolean getDeleteImg5() { return deleteImg5; }
    public void setDeleteImg5(Boolean deleteImg5) { this.deleteImg5 = deleteImg5; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public LocalDate getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(LocalDate fechaBaja) { this.fechaBaja = fechaBaja; }
    
    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
    
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    // --- (Opcional) Helpers para saber si hay que borrar ---
    public boolean mustDelete(int slot) {
        return switch (slot) {
            case 1 -> Boolean.TRUE.equals(deleteImg1);
            case 2 -> Boolean.TRUE.equals(deleteImg2);
            case 3 -> Boolean.TRUE.equals(deleteImg3);
            case 4 -> Boolean.TRUE.equals(deleteImg4);
            case 5 -> Boolean.TRUE.equals(deleteImg5);
            default -> false;
        };
    }
}
