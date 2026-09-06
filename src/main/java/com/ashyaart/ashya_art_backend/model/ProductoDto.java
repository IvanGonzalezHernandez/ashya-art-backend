package com.ashyaart.ashya_art_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductoDto {

    private Long id;
    private String nombre;
    private String subtitulo;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;

    private Boolean estado;
    private LocalDate fechaBaja;

    private String categoria;
    private String medidas;
    private String material;

    private String img1Url;
    private String img2Url;
    private String img3Url;
    private String img4Url;
    private String img5Url;
    
    private Boolean deleteImg1;
    private Boolean deleteImg2;
    private Boolean deleteImg3;
    private Boolean deleteImg4;
    private Boolean deleteImg5;

    public ProductoDto() {}

    public ProductoDto(Long id, String nombre, String subtitulo, String descripcion, BigDecimal precio,
                       Integer stock, Boolean estado, LocalDate fechaBaja,
                       String categoria, String medidas, String material) {
        this.id = id;
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
        this.fechaBaja = fechaBaja;
        this.categoria = categoria;
        this.medidas = medidas;
        this.material = material;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMedidas() {
        return medidas;
    }

    public void setMedidas(String medidas) {
        this.medidas = medidas;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getImg1Url() {
        return img1Url;
    }

    public void setImg1Url(String img1Url) {
        this.img1Url = img1Url;
    }

    public String getImg2Url() {
        return img2Url;
    }

    public void setImg2Url(String img2Url) {
        this.img2Url = img2Url;
    }

    public String getImg3Url() {
        return img3Url;
    }

    public void setImg3Url(String img3Url) {
        this.img3Url = img3Url;
    }

    public String getImg4Url() {
        return img4Url;
    }

    public void setImg4Url(String img4Url) {
        this.img4Url = img4Url;
    }

    public String getImg5Url() {
        return img5Url;
    }

    public void setImg5Url(String img5Url) {
        this.img5Url = img5Url;
    }
    
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
}
