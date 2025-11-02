package com.ashyaart.ashya_art_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "CARRITO")
public class Carrito {
    @Id
    @Column(length = 64)
    private String id; // usa el UUID que generes como client_reference_id

    @Lob
    @Column(name = "carrito_json", columnDefinition = "LONGTEXT", nullable = false)
    private String carritoJson;

    @Lob
    @Column(name = "cliente_json", columnDefinition = "LONGTEXT", nullable = false)
    private String clienteJson;

    @Column(nullable = false)
    private boolean consumido = false;

    @Column(nullable = false)
    private LocalDateTime creado = LocalDateTime.now();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClienteJson() {
		return clienteJson;
	}

	public void setClienteJson(String clienteJson) {
		this.clienteJson = clienteJson;
	}

	public String getCarritoJson() {
		return carritoJson;
	}

	public void setCarritoJson(String carritoJson) {
		this.carritoJson = carritoJson;
	}

	public boolean isConsumido() {
		return consumido;
	}

	public void setConsumido(boolean consumido) {
		this.consumido = consumido;
	}

	public LocalDateTime getCreado() {
		return creado;
	}

	public void setCreado(LocalDateTime creado) {
		this.creado = creado;
	}
    
    
}

