package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.CursoAssembler;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.repository.CursoDao;

@Service
public class CursoService {

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);

    @Autowired
    private CursoDao cursoDao;

    public List<CursoDto> findByFilter(CursoFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de cursos con filtro: {}", filter);
        List<Curso> cursos = cursoDao.findByFiltros(filter.getNombre());
        List<CursoDto> resultado = cursos.stream().map(CursoAssembler::toDto).toList();
        logger.info("findByFilter - Se encontraron {} cursos con el filtro", resultado.size());
        return resultado;
    }

    @Transactional
    public CursoDto crearCurso(CursoDto cursoDto) {
        logger.info("crearCurso - Creando nuevo curso: {}", cursoDto);
        Curso curso = CursoAssembler.toEntity(cursoDto);
        curso.setId(null);
        Curso cursoGuardado = cursoDao.save(curso);
        CursoDto dtoGuardado = CursoAssembler.toDto(cursoGuardado);
        logger.info("crearCurso - Curso creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public CursoDto actualizarCurso(CursoDto cursoDto) {
        logger.info("actualizarCurso - Actualizando curso con ID: {}", cursoDto.getId());
        Curso curso = cursoDao.findById(cursoDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoDto.getId()));

        curso.setNombre(cursoDto.getNombre());
        curso.setSubtitulo(cursoDto.getSubtitulo());
        curso.setDescripcion(cursoDto.getDescripcion());
        curso.setPrecio(cursoDto.getPrecio());
        curso.setEstado(cursoDto.getEstado());
        curso.setFechaBaja(cursoDto.getFechaBaja());

        Curso cursoActualizado = cursoDao.save(curso);
        CursoDto dtoActualizado = CursoAssembler.toDto(cursoActualizado);
        logger.info("actualizarCurso - Curso actualizado con ID: {}", dtoActualizado.getId());
        return dtoActualizado;
    }

    @Transactional
    public void eliminarCurso(Long id) {
        logger.info("eliminarCurso - Intentando eliminar curso con ID: {}", id);
        if (!cursoDao.existsById(id)) {
            logger.warn("eliminarCurso - Curso con ID {} no encontrado", id);
            throw new RuntimeException("Curso con id " + id + " no encontrado");
        }
        Integer filasAfectadas = cursoDao.borradoLogico(id);
        if (filasAfectadas == 0) {
            logger.error("eliminarCurso - No se pudo eliminar el curso con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar el curso con id " + id);
        }
        logger.info("eliminarCurso - Curso con ID {} eliminado correctamente (borrado lógico)", id);
    }
}
