package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.CursoFechaAssembler;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.filter.CursoFechaFilter;
import com.ashyaart.ashya_art_backend.model.CursoFechaDto;
import com.ashyaart.ashya_art_backend.repository.CursoDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;

@Service
public class CursoFechaService {

    private static final Logger logger = LoggerFactory.getLogger(CursoFechaService.class);

    @Autowired
    private CursoFechaDao cursoFechaDao;
    
    @Autowired
    private CursoDao cursoDao;

    public List<CursoFechaDto> findByFilter(CursoFechaFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de fechas con filtro: {}", filter);
        List<CursoFecha> fechas = cursoFechaDao.findByFiltros(filter.getFecha());
        List<CursoFechaDto> resultado = fechas.stream().map(CursoFechaAssembler::toDto).toList();
        logger.info("findByFilter - Se encontraron {} fechas con el filtro", resultado.size());
        return resultado;
    }

    @Transactional
    public CursoFechaDto crearFecha(CursoFechaDto cursoFechaDto) {
        logger.info("crearFecha - Creando nueva fecha: {}", cursoFechaDto);
        CursoFecha fecha = CursoFechaAssembler.toEntity(cursoFechaDto);
        fecha.setId(null);
        CursoFecha fechaGuardada = cursoFechaDao.save(fecha);
        CursoFechaDto dtoGuardado = CursoFechaAssembler.toDto(fechaGuardada);
        logger.info("crearFecha - Fecha creada con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public CursoFechaDto actualizarFecha(CursoFechaDto cursoFechaDto) {
        logger.info("actualizarFecha - Actualizando fecha con ID: {}", cursoFechaDto.getId());
        
        CursoFecha fecha = cursoFechaDao.findById(cursoFechaDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Fecha no encontrada con ID: " + cursoFechaDto.getId()));
        
        Curso curso = cursoDao.findById(cursoFechaDto.getIdCurso())
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoFechaDto.getIdCurso()));
        
        fecha.setCurso(curso);
        fecha.setFecha(cursoFechaDto.getFecha());
        fecha.setHoraInicio(cursoFechaDto.getHoraInicio());
        fecha.setHoraFin(cursoFechaDto.getHoraFin());
        fecha.setPlazasDisponibles(cursoFechaDto.getPlazasDisponibles());

        CursoFecha fechaActualizada = cursoFechaDao.save(fecha);
        CursoFechaDto dtoActualizado = CursoFechaAssembler.toDto(fechaActualizada);

        logger.info("actualizarFecha - Fecha actualizada con ID: {}", dtoActualizado.getId());
        return dtoActualizado;
    }

    @Transactional
    public void eliminarFecha(Long id) {
        logger.info("eliminarFecha - Intentando eliminar fecha con ID: {}", id);
        if (!cursoFechaDao.existsById(id)) {
            logger.warn("eliminarFecha - Fecha con ID {} no encontrada", id);
            throw new RuntimeException("Fecha con id " + id + " no encontrada");
        }
        Integer filasAfectadas = cursoFechaDao.borradoLogico(id);
        if (filasAfectadas == 0) {
            logger.error("eliminarFecha - No se pudo eliminar la fecha con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar la fecha con id " + id);
        }
        logger.info("eliminarFecha - Fecha con ID {} eliminada correctamente (borrado lógico)", id);
    }
}
