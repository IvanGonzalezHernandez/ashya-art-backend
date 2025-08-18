package com.ashyaart.ashya_art_backend.model;

import java.util.List;

public class CarritoDto {
	private List<ItemCarritoDto> items;

    // Getters y setters
    public List<ItemCarritoDto> getItems() { return items; }
    public void setItems(List<ItemCarritoDto> items) { this.items = items; }
}