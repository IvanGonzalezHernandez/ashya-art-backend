package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.entity.Secreto;
import com.ashyaart.ashya_art_backend.model.SecretoDto;

public class SecretoAssembler {

    public static SecretoDto toDto(Secreto secreto) {
        SecretoDto dto = new SecretoDto();
        dto.setId(secreto.getId());
        dto.setEstado(secreto.getEstado());
        dto.setFechaBaja(secreto.getFechaBaja());
        dto.setPrecio(secreto.getPrecio());
        dto.setNombre(secreto.getNombre());
        dto.setSubtitulo(secreto.getSubtitulo());
        dto.setDescripcion(secreto.getDescripcion());
        dto.setCategoria(secreto.getCategoria());
        dto.setPdf(secreto.getPdf());
        dto.setImg1(secreto.getImg1());
        dto.setImg2(secreto.getImg2());
        dto.setImg3(secreto.getImg3());
        dto.setImg4(secreto.getImg4());
        dto.setImg5(secreto.getImg5());
        return dto;
    }

    public static Secreto toEntity(SecretoDto dto) {
        Secreto secreto = new Secreto();
        secreto.setId(dto.getId());
        secreto.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        secreto.setFechaBaja(dto.getFechaBaja());
        secreto.setPrecio(dto.getPrecio());
        secreto.setNombre(dto.getNombre());
        secreto.setSubtitulo(dto.getSubtitulo());
        secreto.setDescripcion(dto.getDescripcion());
        secreto.setCategoria(dto.getCategoria());
        secreto.setPdf(dto.getPdf());
        secreto.setImg1(dto.getImg1());
        secreto.setImg2(dto.getImg2());
        secreto.setImg3(dto.getImg3());
        secreto.setImg4(dto.getImg4());
        secreto.setImg5(dto.getImg5());
        return secreto;
    }
}
