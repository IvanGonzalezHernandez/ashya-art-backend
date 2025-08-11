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

        dto.setImg1(producto.getImg1());
        dto.setImg2(producto.getImg2());
        dto.setImg3(producto.getImg3());
        dto.setImg4(producto.getImg4());
        dto.setImg5(producto.getImg5());

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

        producto.setImg1(dto.getImg1());
        producto.setImg2(dto.getImg2());
        producto.setImg3(dto.getImg3());
        producto.setImg4(dto.getImg4());
        producto.setImg5(dto.getImg5());

        return producto;
    }
}
