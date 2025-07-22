package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.model.CursoFechaDto;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.entity.Curso;

public class CursoFechaAssembler {

    public static CursoFechaDto toDto(CursoFecha cursoFecha) {
        CursoFechaDto dto = new CursoFechaDto();
        dto.setId(cursoFecha.getId());
        dto.setIdCurso(cursoFecha.getCurso() != null ? cursoFecha.getCurso().getId() : null);
        dto.setFecha(cursoFecha.getFecha());
        dto.setHoraInicio(cursoFecha.getHoraInicio());
        dto.setHoraFin(cursoFecha.getHoraFin());
        dto.setPlazasDisponibles(cursoFecha.getPlazasDisponibles());
        dto.setNombreCurso(cursoFecha.getCurso() != null ? cursoFecha.getCurso().getNombre() : null);
        return dto;
    }
    
    public static CursoFecha toEntity(CursoFechaDto dto) {
        CursoFecha cursoFecha = new CursoFecha();
        cursoFecha.setId(dto.getId());

        if (dto.getIdCurso() != null) {
            Curso curso = new Curso();
            curso.setId(dto.getIdCurso());
            cursoFecha.setCurso(curso);
        } else {
            cursoFecha.setCurso(null);
        }

        cursoFecha.setFecha(dto.getFecha());
        cursoFecha.setHoraInicio(dto.getHoraInicio());
        cursoFecha.setHoraFin(dto.getHoraFin());
        cursoFecha.setPlazasDisponibles(dto.getPlazasDisponibles());

        return cursoFecha;
    }
}
