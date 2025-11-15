package com.ashyaart.ashya_art_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "COMPRA")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "carrito_id")
    private String carritoId;

    @Column(name = "codigo_compra", nullable = false, unique = true, length = 36)
    private String codigoCompra;
    
    @Column(nullable = false)
    private Boolean pagado;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    // Relación con items de cursos, productos, secretos y tarjetas regalo
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CursoCompra> cursosComprados;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoCompra> productosComprados;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecretoCompra> secretosComprados;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarjetaRegaloCompra> tarjetasRegaloCompradas;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoCompra() { return codigoCompra; }
    public void setCodigoCompra(String codigoCompra) { this.codigoCompra = codigoCompra; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<CursoCompra> getCursosComprados() { return cursosComprados; }
    public void setCursosComprados(List<CursoCompra> cursosComprados) { this.cursosComprados = cursosComprados; }

    public List<ProductoCompra> getProductosComprados() { return productosComprados; }
    public void setProductosComprados(List<ProductoCompra> productosComprados) { this.productosComprados = productosComprados; }

    public List<SecretoCompra> getSecretosComprados() { return secretosComprados; }
    public void setSecretosComprados(List<SecretoCompra> secretosComprados) { this.secretosComprados = secretosComprados; }

    public List<TarjetaRegaloCompra> getTarjetasRegaloCompradas() { return tarjetasRegaloCompradas; }
    public void setTarjetasRegaloCompradas(List<TarjetaRegaloCompra> tarjetasRegaloCompradas) { this.tarjetasRegaloCompradas = tarjetasRegaloCompradas; }
    
    public Boolean getPagado() { return pagado; }
    public void setPagado(Boolean pagado) { this.pagado = pagado; }
    
    public String getCarritoId() { return carritoId; }
    public void setCarritoId(String carritoId) { this.carritoId = carritoId; }
}
