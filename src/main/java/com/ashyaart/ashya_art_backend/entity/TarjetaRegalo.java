package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "TARJETA_REGALO")
public class TarjetaRegalo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(name = "ID_REFERENCIA", unique = true, nullable = false)
    private String idReferencia;

    private String img;

    @Column(name = "FECHA_ALTA")
    private LocalDate fechaAlta;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    private boolean estado = true;
    
    @OneToMany(mappedBy = "tarjetaRegalo", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TarjetaRegaloCompra> compras;

    public TarjetaRegalo() {}

    public TarjetaRegalo(String nombre, BigDecimal precio, String idReferencia, String img, LocalDate fechaAlta, LocalDate fechaBaja, boolean estado, List<TarjetaRegaloCompra> compras) {
        this.nombre = nombre;
        this.precio = precio;
        this.idReferencia = idReferencia;
        this.img = img;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
        this.estado = estado;
        this.compras = compras;
    }

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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(String idReferencia) {
        this.idReferencia = idReferencia;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
	public List<TarjetaRegaloCompra> getCompras() {
		return compras;
	}
	
	public void setCompras(List<TarjetaRegaloCompra> compras) {
		this.compras = compras;
	}
}
