package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloCompraDto;

public class TarjetaRegaloCompraAssembler {

    public static TarjetaRegaloCompraDto toDto(TarjetaRegaloCompra entity) {
        TarjetaRegaloCompraDto dto = new TarjetaRegaloCompraDto();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setDestinatario(entity.getDestinatario());
        dto.setCanjeada(entity.isCanjeada());
        dto.setFechaCompra(entity.getFechaCompra());
        dto.setFechaCaducidad(entity.getFechaCaducidad());
        dto.setFechaBaja(entity.getFechaBaja());
        dto.setEstado(entity.isEstado());

        dto.setIdTarjeta(entity.getTarjetaRegalo() != null ? entity.getTarjetaRegalo().getId() : null);
        dto.setIdCliente(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setIdCompra(entity.getCompra() != null ? entity.getCompra().getId() : null);
        
        dto.setEmail(entity.getCliente() != null ? entity.getCliente().getEmail() : null);
        dto.setPrecio(entity.getTarjetaRegalo() != null ? entity.getTarjetaRegalo().getPrecio() : null);

        return dto;
    }

    public static TarjetaRegaloCompra toEntity(TarjetaRegaloCompraDto dto) {
        TarjetaRegaloCompra entity = new TarjetaRegaloCompra();
        entity.setId(dto.getId());
        entity.setCodigo(dto.getCodigo());
        entity.setDestinatario(dto.getDestinatario());
        entity.setCanjeada(dto.getCanjeada() != null ? dto.getCanjeada() : false);
        entity.setFechaCompra(dto.getFechaCompra());
        entity.setFechaCaducidad(dto.getFechaCaducidad());
        entity.setFechaBaja(dto.getFechaBaja());
        entity.setEstado(dto.getEstado() != null ? dto.getEstado() : true);

        return entity;
    }
}
