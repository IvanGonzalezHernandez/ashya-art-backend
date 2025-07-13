package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.model.ProductoDto;
import com.ashyaart.ashya_art_backend.entity.Producto;

public class ProductoAssembler {

    public static ProductoDto toDto(Producto producto) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setSubtitulo(producto.getSubtitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImg(producto.getImg());
        dto.setEstado(producto.getEstado());
        dto.setFechaBaja(producto.getFechaBaja());
        return dto;
    }

    public static Producto toEntity(ProductoDto dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setSubtitulo(dto.getSubtitulo());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setImg(dto.getImg());
        producto.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        producto.setFechaBaja(dto.getFechaBaja());
        return producto;
    }
}
