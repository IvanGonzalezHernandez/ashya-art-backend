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
    
	public static Curso toEntity(CursoDto dto) {

		Curso curso = new Curso();
		curso.setId(dto.getId());
		curso.setNombre(dto.getNombre());
		curso.setSubtitulo(dto.getSubtitulo());
		curso.setDescripcion(dto.getDescripcion());
		curso.setPrecio(dto.getPrecio());
		curso.setImg(dto.getImg());
		curso.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
		curso.setFechaBaja(dto.getFechaBaja());

		return curso;
	}
}
