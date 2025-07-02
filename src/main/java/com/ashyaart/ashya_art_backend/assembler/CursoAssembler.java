package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.entity.Curso;

public class CursoAssembler {

    public static CursoDto toDto(Curso curso) {

        CursoDto dto = new CursoDto();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setSubtitulo(curso.getSubtitulo());
        dto.setDescripcion(curso.getDescripcion());
        dto.setPrecio(curso.getPrecio());
        dto.setImg(curso.getImg());
        dto.setEstado(curso.getEstado());
        dto.setFechaBaja(curso.getFechaBaja());

        return dto;
    }
}
