package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.entity.ProductoCompra;
import com.ashyaart.ashya_art_backend.model.ProductoCompraDto;

public class ProductoCompraAssembler {

    public static ProductoCompraDto toDto(ProductoCompra entity) {
        if (entity == null) return null;

        ProductoCompraDto dto = new ProductoCompraDto();
        dto.setId(entity.getId());

        if (entity.getCliente() != null) {
            dto.setIdCliente(entity.getCliente().getId());
            dto.setNombreCliente(entity.getCliente().getNombre());
            dto.setEmailCliente(entity.getCliente().getEmail());
        }

        if (entity.getProducto() != null) {
            dto.setIdProducto(entity.getProducto().getId());
            dto.setNombreProducto(entity.getProducto().getNombre());
        }

        dto.setCantidad(entity.getCantidad());
        dto.setFechaCompra(entity.getFechaCompra());
        dto.setPrecio(entity.getPrecio());
        dto.setNumeroSeguimiento(entity.getNumeroSeguimiento());
        dto.setMetodoEnvio(entity.getMetodoEnvio());

        return dto;
    }

    public static ProductoCompra toEntity(ProductoCompraDto dto, Cliente cliente, Producto producto) {
        if (dto == null || cliente == null || producto == null) return null;

        ProductoCompra entity = new ProductoCompra();
        entity.setId(dto.getId());
        entity.setCliente(cliente);
        entity.setProducto(producto);
        entity.setCantidad(dto.getCantidad());
        entity.setFechaCompra(dto.getFechaCompra());
        entity.setPrecio(dto.getPrecio());

        return entity;
    }
}
