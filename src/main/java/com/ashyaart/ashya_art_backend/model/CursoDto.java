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
    private BigDecimal precio;

    // Imágenes en bytes (persistidas)
    private byte[] img1;
    private byte[] img2;
    private byte[] img3;
    private byte[] img4;
    private byte[] img5;

    // Flags para borrar imágenes concretas en actualización
    private Boolean deleteImg1;
    private Boolean deleteImg2;
    private Boolean deleteImg3;
    private Boolean deleteImg4;
    private Boolean deleteImg5;

    private Boolean estado;
    private LocalDate fechaBaja;
    private Integer plazasMaximas;
    private String informacionExtra;

    public CursoDto() {}

    public CursoDto(Long id, String nombre, String subtitulo, String descripcion, BigDecimal precio, Boolean estado,
            LocalDate fechaBaja, String nivel, String duracion, String piezas, String materiales,
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

    public byte[] getImg1() { return img1; }
    public void setImg1(byte[] img1) { this.img1 = img1; }

    public byte[] getImg2() { return img2; }
    public void setImg2(byte[] img2) { this.img2 = img2; }

    public byte[] getImg3() { return img3; }
    public void setImg3(byte[] img3) { this.img3 = img3; }

    public byte[] getImg4() { return img4; }
    public void setImg4(byte[] img4) { this.img4 = img4; }

    public byte[] getImg5() { return img5; }
    public void setImg5(byte[] img5) { this.img5 = img5; }

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
