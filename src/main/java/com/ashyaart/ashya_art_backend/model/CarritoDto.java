package com.ashyaart.ashya_art_backend.model;

import java.util.List;

public class CarritoDto {
	
	private String id;
	private List<ItemCarritoDto> items;

	/** Metodo de entrega elegido cuando el carrito contiene algun PRODUCTO: PICKUP, GERMANY o EU. */
	private String metodoEnvio;

    // Getters y setters
    public List<ItemCarritoDto> getItems() { return items; }
    public void setItems(List<ItemCarritoDto> items) { this.items = items; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMetodoEnvio() { return metodoEnvio; }
    public void setMetodoEnvio(String metodoEnvio) { this.metodoEnvio = metodoEnvio; }
}