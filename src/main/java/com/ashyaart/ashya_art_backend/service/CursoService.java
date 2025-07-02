package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.assembler.CursoAssembler;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.repository.CursoDao;

@Service
public class CursoService {

    @Autowired
    private CursoDao cursoDao;

    public List<CursoDto> findByFilter(CursoFilter filter) {
        List<Curso> cursos = cursoDao.findByFiltros(
            filter.getNombre()
        );
        return cursos.stream().map(CursoAssembler::toDto).toList();
    }
}