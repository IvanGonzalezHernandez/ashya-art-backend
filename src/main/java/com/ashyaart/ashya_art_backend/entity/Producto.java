package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PRODUCTO")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String subtitulo;

    @Lob
    @NotBlank
    @Column(nullable = false)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal precio;

    @NotNull
    @Column(nullable = false)
    private Integer stock;
    
    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Boolean estado;
    
    @NotBlank
    @Column(nullable = false, length = 100)
    private String categoria;
    
    @NotBlank
    @Column(nullable = false, length = 100)
    private String medidas;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String material;

    @Column(name = "img1_url", length = 255)
    private String img1Url;

    @Column(name = "img2_url", length = 255)
    private String img2Url;

    @Column(name = "img3_url", length = 255)
    private String img3Url;

    @Column(name = "img4_url", length = 255)
    private String img4Url;

    @Column(name = "img5_url", length = 255)
    private String img5Url;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductoCompra> compras;

    public Producto() {}

    public Producto(String nombre, String subtitulo, String descripcion, BigDecimal precio, Integer stock,
                    Boolean estado, LocalDate fechaBaja, String categoria, String medidas, String material) {
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
	
	public List<ProductoCompra> getCompras() {
		return compras;
	}
	
	public void setCompras(List<ProductoCompra> compras) {
		this.compras = compras;
	}
}
