package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 150)
    private String subtitulo;

    @NotBlank
    @Lob
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precio;

    @Size(max = 50)
    private String nivel;

    @Size(max = 50)
    private String idioma;

    @Size(max = 255)
    private String img;
    
    private Integer estado = 1;
    
    private LocalDate fechaBaja;

    public Curso() {
    }

    public Curso(String nombre, String subtitulo, String descripcion, BigDecimal precio, String nivel, String idioma, String img, Integer estado, LocalDate fechaBaja) {
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.nivel = nivel;
        this.idioma = idioma;
        this.img = img;
        this.estado = estado;
        this.fechaBaja = fechaBaja;
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

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public LocalDate getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(LocalDate fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
}
