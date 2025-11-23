package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String apellido;

    @NotBlank
    @Column(nullable = false)
    private String calle;

    @NotBlank
    @Column(nullable = false)
    private String numero;

    private String piso;

    @NotBlank
    @Column(nullable = false)
    private String ciudad;
    
    @Column(nullable = false)
    private String pais;

    @NotBlank
    @Column(name = "CODIGO_POSTAL", nullable = false)
    private String codigoPostal;

    @NotBlank
    @Column(nullable = false)
    private String provincia;

    @Column(length = 20)
    private String telefono;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "FECHA_ALTA", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CursoCompra> comprasCursos;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductoCompra> comprasProductos;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TarjetaRegaloCompra> comprasTarjetas;

    public Cliente() {}

    public Cliente(String nombre, String apellido, String calle, String numero, String piso,
                   String ciudad, String codigoPostal, String provincia, String telefono,
                   String email, LocalDate fechaAlta, LocalDate fechaBaja, List<CursoCompra> comprasCursos,
                   List<ProductoCompra> comprasProductos, List<TarjetaRegaloCompra> comprasTarjetas,
                   String pais) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.calle = calle;
        this.numero = numero;
        this.piso = piso;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.provincia = provincia;
        this.telefono = telefono;
        this.email = email;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
        this.comprasCursos = comprasCursos;
        this.comprasProductos = comprasProductos;
        this.comprasTarjetas = comprasTarjetas;
        this.pais = pais;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
	public String getPais() {
		return pais;
	}
	
	public void setPais(String pais) {
		this.pais = pais;
	}

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

	public List<CursoCompra> getComprasCursos() {
		return comprasCursos;
	}

	public void setComprasCursos(List<CursoCompra> comprasCursos) {
		this.comprasCursos = comprasCursos;
	}

	public List<ProductoCompra> getComprasProductos() {
		return comprasProductos;
	}

	public void setComprasProductos(List<ProductoCompra> comprasProductos) {
		this.comprasProductos = comprasProductos;
	}

	public List<TarjetaRegaloCompra> getComprasTarjetas() {
		return comprasTarjetas;
	}

	public void setComprasTarjetas(List<TarjetaRegaloCompra> comprasTarjetas) {
		this.comprasTarjetas = comprasTarjetas;
	}
}
