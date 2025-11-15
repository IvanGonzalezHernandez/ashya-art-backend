package com.ashyaart.ashya_art_backend.model;

import java.util.List;

public class CarritoDto {
	
	private String id;
	private List<ItemCarritoDto> items;

    // Getters y setters
    public List<ItemCarritoDto> getItems() { return items; }
    public void setItems(List<ItemCarritoDto> items) { this.items = items; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}