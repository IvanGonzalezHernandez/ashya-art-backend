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
        dto.setEstado(producto.getEstado());
        dto.setFechaBaja(producto.getFechaBaja());

        dto.setCategoria(producto.getCategoria());
        dto.setMedidas(producto.getMedidas());
        dto.setMaterial(producto.getMaterial());

        dto.setImg1Url(producto.getImg1Url());
        dto.setImg2Url(producto.getImg2Url());
        dto.setImg3Url(producto.getImg3Url());
        dto.setImg4Url(producto.getImg4Url());
        dto.setImg5Url(producto.getImg5Url());

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
        producto.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        producto.setFechaBaja(dto.getFechaBaja());

        producto.setCategoria(dto.getCategoria());
        producto.setMedidas(dto.getMedidas());
        producto.setMaterial(dto.getMaterial());

        producto.setImg1Url(dto.getImg1Url());
        producto.setImg2Url(dto.getImg2Url());
        producto.setImg3Url(dto.getImg3Url());
        producto.setImg4Url(dto.getImg4Url());
        producto.setImg5Url(dto.getImg5Url());

        return producto;
    }
}
