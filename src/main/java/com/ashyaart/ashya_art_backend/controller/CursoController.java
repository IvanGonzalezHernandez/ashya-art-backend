package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.service.CursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {
	
	private static final Logger logger = LoggerFactory.getLogger(CursoController.class);

    @Autowired
    private CursoService cursoService;


    @GetMapping
    public ResponseEntity<List<CursoDto>> findByFilter(CursoFilter filter) {
        logger.info("findByFilter - Solicitud GET para filtrar cursos con filtro: {}", filter);
        List<CursoDto> cursosDto = cursoService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} cursos con el filtro proporcionado", cursosDto.size());
        return ResponseEntity.ok(cursosDto);
    }

    @PostMapping
    public ResponseEntity<CursoDto> crearCurso(@RequestBody CursoDto cursoDto) {
        logger.info("crearCurso - Solicitud POST para crear un nuevo curso: {}", cursoDto);
        CursoDto nuevoCurso = cursoService.crearCurso(cursoDto);
        logger.info("crearCurso - Curso creado con ID: {}", nuevoCurso.getId());
        return ResponseEntity.ok(nuevoCurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoDto> actualizarCurso(@PathVariable Long id, @RequestBody CursoDto cursoDto) {
        logger.info("actualizarCurso - Solicitud PUT para actualizar curso con ID {}: {}", id, cursoDto);
        cursoDto.setId(id);
        CursoDto cursoActualizado = cursoService.actualizarCurso(cursoDto);
        logger.info("actualizarCurso - Curso actualizado con ID: {}", cursoActualizado.getId());
        return ResponseEntity.ok(cursoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        logger.info("eliminarCurso - Solicitud DELETE para eliminar curso con ID: {}", id);
        cursoService.eliminarCurso(id);
        logger.info("eliminarCurso - Curso con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
