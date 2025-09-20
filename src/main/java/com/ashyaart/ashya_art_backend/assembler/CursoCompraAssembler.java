package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.model.CursoCompraDto;

public class CursoCompraAssembler {

    public static CursoCompraDto toDto(CursoCompra entity) {
        CursoCompraDto dto = new CursoCompraDto();
        dto.setId(entity.getId());
        dto.setIdFecha(entity.getCursoFecha().getId());
        dto.setIdCliente(entity.getCliente().getId());
        dto.setPlazasReservadas(entity.getPlazasReservadas());
        dto.setFechaReserva(entity.getFechaReserva());
        dto.setNombreCliente(entity.getCliente().getNombre());
        dto.setTelefono(entity.getCliente().getTelefono());
        dto.setEmail(entity.getCliente().getEmail());
        dto.setNombreCurso(entity.getCursoFecha().getCurso().getNombre());
        dto.setFechaCurso(entity.getCursoFecha().getFecha().toString());
        return dto;
    }

    public static CursoCompra toEntity(CursoCompraDto dto, CursoFecha cursoFecha, Cliente cliente) {
        CursoCompra entity = new CursoCompra();
        entity.setPlazasReservadas(dto.getPlazasReservadas());
        entity.setFechaReserva(dto.getFechaReserva());
        entity.setCursoFecha(cursoFecha);
        entity.setCliente(cliente);
        return entity;
    }
}
