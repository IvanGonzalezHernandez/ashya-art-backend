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

    @NotBlank
    @Column(nullable = false)
    private String img;
    
    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Boolean estado;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductoCompra> compras;

    public Producto() {}

    public Producto(String nombre, String subtitulo, String descripcion, BigDecimal precio, Integer stock, String img, Boolean estado, LocalDate fechaBaja, List<ProductoCompra> compras) {
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.img = img;
        this.estado = estado;
        this.fechaBaja = fechaBaja;
        this.compras = compras;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
	
	public List<ProductoCompra> getCompras() {
		return compras;
	}
	
	public void setCompras(List<ProductoCompra> compras) {
		this.compras = compras;
	}
}
