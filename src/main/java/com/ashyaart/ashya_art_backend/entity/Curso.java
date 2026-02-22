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

    @Lob
    @Column(name = "img1", columnDefinition = "LONGBLOB")
    private byte[] img1;

    @Lob
    @Column(name = "img2", columnDefinition = "LONGBLOB")
    private byte[] img2;

    @Lob
    @Column(name = "img3", columnDefinition = "LONGBLOB")
    private byte[] img3;

    @Lob
    @Column(name = "img4", columnDefinition = "LONGBLOB")
    private byte[] img4;

    @Lob
    @Column(name = "img5", columnDefinition = "LONGBLOB")
    private byte[] img5;
    
    @Column(name = "FECHA_BAJA")
    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Boolean estado;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CursoFecha> fechas;

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
}
