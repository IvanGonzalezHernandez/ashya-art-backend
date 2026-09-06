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
	    dto.setNivel(curso.getNivel());
	    dto.setDuracion(curso.getDuracion());
	    dto.setPiezas(curso.getPiezas());
	    dto.setMateriales(curso.getMateriales());
	    dto.setLocalizacion(curso.getLocalizacion());
	    dto.setPrecio(curso.getPrecio());
	    dto.setImg1Url(curso.getImg1Url());
	    dto.setImg2Url(curso.getImg2Url());
	    dto.setImg3Url(curso.getImg3Url());
	    dto.setImg4Url(curso.getImg4Url());
	    dto.setImg5Url(curso.getImg5Url());
	    dto.setEstado(curso.getEstado());
	    dto.setFechaBaja(curso.getFechaBaja());
	    dto.setPlazasMaximas(curso.getPlazasMaximas());
	    dto.setOrden(curso.getOrden());

	    return dto;
	}

    
	public static Curso toEntity(CursoDto dto) {

	    Curso curso = new Curso();
	    curso.setId(dto.getId());
	    curso.setNombre(dto.getNombre());
	    curso.setSubtitulo(dto.getSubtitulo());
	    curso.setDescripcion(dto.getDescripcion());
	    curso.setNivel(dto.getNivel());
	    curso.setDuracion(dto.getDuracion());
	    curso.setPiezas(dto.getPiezas());
	    curso.setMateriales(dto.getMateriales());
	    curso.setLocalizacion(dto.getLocalizacion());
	    curso.setPrecio(dto.getPrecio());
	    curso.setImg1Url(dto.getImg1Url());
	    curso.setImg2Url(dto.getImg2Url());
	    curso.setImg3Url(dto.getImg3Url());
	    curso.setImg4Url(dto.getImg4Url());
	    curso.setImg5Url(dto.getImg5Url());
	    curso.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
	    curso.setFechaBaja(dto.getFechaBaja());
	    curso.setPlazasMaximas(dto.getPlazasMaximas());
	    curso.setOrden(dto.getOrden());

	    return curso;
	}

}
