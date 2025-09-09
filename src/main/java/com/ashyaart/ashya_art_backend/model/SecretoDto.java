package com.ashyaart.ashya_art_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SecretoDto {

    private Long id;
    private Boolean estado;
    private LocalDate fechaBaja;
    private BigDecimal precio;
    private String nombre;
    private String subtitulo;
    private String descripcion;
    private String categoria;
    private byte[] pdf;
    private byte[] img1;
    private byte[] img2;
    private byte[] img3;
    private byte[] img4;
    private byte[] img5;
    
    private Boolean deleteImg1;
    private Boolean deleteImg2;
    private Boolean deleteImg3;
    private Boolean deleteImg4;
    private Boolean deleteImg5;
    private Boolean deletePdf;

    public SecretoDto() {}

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public byte[] getImg1() {
        return img1;
    }

    public void setImg1(byte[] img1) {
        this.img1 = img1;
    }

    public byte[] getImg2() {
        return img2;
    }

    public void setImg2(byte[] img2) {
        this.img2 = img2;
    }

    public byte[] getImg3() {
        return img3;
    }

    public void setImg3(byte[] img3) {
        this.img3 = img3;
    }

    public byte[] getImg4() {
        return img4;
    }

    public void setImg4(byte[] img4) {
        this.img4 = img4;
    }

    public byte[] getImg5() {
        return img5;
    }

    public void setImg5(byte[] img5) {
        this.img5 = img5;
    }

	public Boolean getDeleteImg1() {
		return deleteImg1;
	}

	public void setDeleteImg1(Boolean deleteImg1) {
		this.deleteImg1 = deleteImg1;
	}

	public Boolean getDeleteImg2() {
		return deleteImg2;
	}

	public void setDeleteImg2(Boolean deleteImg2) {
		this.deleteImg2 = deleteImg2;
	}

	public Boolean getDeleteImg3() {
		return deleteImg3;
	}

	public void setDeleteImg3(Boolean deleteImg3) {
		this.deleteImg3 = deleteImg3;
	}

	public Boolean getDeleteImg4() {
		return deleteImg4;
	}

	public void setDeleteImg4(Boolean deleteImg4) {
		this.deleteImg4 = deleteImg4;
	}

	public Boolean getDeleteImg5() {
		return deleteImg5;
	}

	public void setDeleteImg5(Boolean deleteImg5) {
		this.deleteImg5 = deleteImg5;
	}

	public Boolean getDeletePdf() {
		return deletePdf;
	}

	public void setDeletePdf(Boolean deletePdf) {
		this.deletePdf = deletePdf;
	}
    
    
}
