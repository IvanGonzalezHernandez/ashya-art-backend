package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "CURSO")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String subtitulo;

    @Lob
    private String descripcion;
    
    @Lob
    private String nivel;

    @Lob
    private String duracion;

    @Lob
    private String piezas;

    @Lob
    private String materiales;
    
    private String localizacion;
    
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precio;

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
    
    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Boolean estado;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CursoFecha> fechas;
    
    @Column(name = "ORDEN")
    private Integer orden;

    public Curso() {
    }

    public Curso(String nombre, String subtitulo, String descripcion, BigDecimal precio, Boolean estado, LocalDate fechaBaja,
            List<CursoFecha> fechas, String nivel, String duracion, String piezas, String materiales, String localizacion,
            Integer plazasMaximas) {
	   this.nombre = nombre;
	   this.subtitulo = subtitulo;
	   this.descripcion = descripcion;
	   this.precio = precio;
	   this.estado = estado;
	   this.fechaBaja = fechaBaja;
	   this.fechas = fechas;
	   this.nivel = nivel;
	   this.duracion = duracion;
	   this.piezas = piezas;
	   this.materiales = materiales;
	   this.localizacion = localizacion;
	   this.plazasMaximas = plazasMaximas;
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
    
    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getPiezas() {
        return piezas;
    }

    public void setPiezas(String piezas) {
        this.piezas = piezas;
    }

    public String getMateriales() {
        return materiales;
    }

    public void setMateriales(String materiales) {
        this.materiales = materiales;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    @Column(name = "plazas_maximas")
    private Integer plazasMaximas;
    
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
	
	public List<CursoFecha> getFechas() {
		return fechas;
	}
	
	public void setFechas(List<CursoFecha> fechas) {
		this.fechas = fechas;
	}
	
    public Integer getPlazasMaximas() {
        return plazasMaximas;
    }

    public void setPlazasMaximas(Integer plazasMaximas) {
        this.plazasMaximas = plazasMaximas;
    }
    
	public String getLocalizacion() {
		return localizacion;
	}
	
	public void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
	}
	
	public Integer getOrden() {
		return orden;
	}
	
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	
}
