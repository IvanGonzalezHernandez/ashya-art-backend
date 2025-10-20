package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.entity.SecretoCompra;
import com.ashyaart.ashya_art_backend.model.SecretoCompraDto;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Secreto;
import com.ashyaart.ashya_art_backend.entity.Compra;

public class SecretoCompraAssembler {

    public static SecretoCompraDto toDto(SecretoCompra entity) {
        SecretoCompraDto dto = new SecretoCompraDto();
        dto.setId(entity.getId());
        dto.setFechaCompra(entity.getFechaCompra());
        dto.setClienteId(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setSecretoId(entity.getSecreto() != null ? entity.getSecreto().getId() : null);
        dto.setCompraId(entity.getCompra() != null ? entity.getCompra().getId() : null);
        return dto;
    }

    public static SecretoCompra toEntity(SecretoCompraDto dto) {
        SecretoCompra entity = new SecretoCompra();
        entity.setId(dto.getId());
        entity.setFechaCompra(dto.getFechaCompra());

        if (dto.getClienteId() != null) {
            Cliente cliente = new Cliente();
            cliente.setId(dto.getClienteId());
            entity.setCliente(cliente);
        }

        if (dto.getSecretoId() != null) {
            Secreto secreto = new Secreto();
            secreto.setId(dto.getSecretoId());
            entity.setSecreto(secreto);
        }

        if (dto.getCompraId() != null) {
            Compra compra = new Compra();
            compra.setId(dto.getCompraId());
            entity.setCompra(compra);
        }

        return entity;
    }
}
