package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ashyaart.ashya_art_backend.filter.CursoFechaFilter;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.model.CursoFechaDto;
import com.ashyaart.ashya_art_backend.service.CursoFechaService;

@RestController
@RequestMapping("/api/cursos-fecha")
public class CursoFechaController {

    private static final Logger logger = LoggerFactory.getLogger(CursoFechaController.class);

    @Autowired
    private CursoFechaService cursoFechaService;

    @GetMapping
    public ResponseEntity<List<CursoFechaDto>> findByFilter(CursoFechaFilter filter) {
        logger.info("findByFilter - Solicitud GET para filtrar fechas con filtro: {}", filter);
        List<CursoFechaDto> fechasDto = cursoFechaService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} fechas con el filtro proporcionado", fechasDto.size());
        return ResponseEntity.ok(fechasDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<List<CursoFechaDto>> findByIdCurso(@PathVariable Long id) {
        logger.info("findByIdCurso - Solicitud de fechas para curso con ID: {}", id);
        List<CursoFechaDto> fechasDto = cursoFechaService.findByIdCurso(id);
        logger.info("findByIdCurso - Se encontraron {} fechas para el curso con ID: {}", fechasDto.size(), id);
        return ResponseEntity.ok(fechasDto);
    }

    @PostMapping
    public ResponseEntity<CursoFechaDto> crearFecha(@RequestBody CursoFechaDto cursoFechaDto) {
        logger.info("crearFecha - Solicitud POST para crear una nueva fecha: {}", cursoFechaDto);
        CursoFechaDto nuevaFecha = cursoFechaService.crearFecha(cursoFechaDto);
        logger.info("crearFecha - Fecha creada con ID: {}", nuevaFecha.getId());
        return ResponseEntity.ok(nuevaFecha);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoFechaDto> actualizarFecha(@PathVariable Long id, @RequestBody CursoFechaDto cursoFechaDto) {
        logger.info("actualizarFecha - Solicitud PUT para actualizar fecha con ID {}: {}", id, cursoFechaDto);
        cursoFechaDto.setId(id);
        CursoFechaDto fechaActualizada = cursoFechaService.actualizarFecha(cursoFechaDto);
        logger.info("actualizarFecha - Fecha actualizada con ID: {}", fechaActualizada.getId());
        return ResponseEntity.ok(fechaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFecha(@PathVariable Long id) {
        logger.info("eliminarFecha - Solicitud DELETE para eliminar fecha con ID: {}", id);
        cursoFechaService.eliminarFecha(id);
        logger.info("eliminarFecha - Fecha con ID {} eliminada", id);
        return ResponseEntity.noContent().build();
    }
}
