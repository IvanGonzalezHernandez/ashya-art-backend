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
	    dto.setPrecio(curso.getPrecio());
	    dto.setImg1(curso.getImg1());
	    dto.setImg2(curso.getImg2());
	    dto.setImg3(curso.getImg3());
	    dto.setImg4(curso.getImg4());
	    dto.setImg5(curso.getImg5());
	    dto.setEstado(curso.getEstado());
	    dto.setFechaBaja(curso.getFechaBaja());
	    dto.setPlazasMaximas(curso.getPlazasMaximas());
	    dto.setInformacionExtra(curso.getInformacionExtra());

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
	    curso.setPrecio(dto.getPrecio());
	    curso.setImg1(dto.getImg1());
	    curso.setImg2(dto.getImg2());
	    curso.setImg3(dto.getImg3());
	    curso.setImg4(dto.getImg4());
	    curso.setImg5(dto.getImg5());
	    curso.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
	    curso.setFechaBaja(dto.getFechaBaja());
	    curso.setPlazasMaximas(dto.getPlazasMaximas());
	    curso.setInformacionExtra(dto.getInformacionExtra());

	    return curso;
	}

}
