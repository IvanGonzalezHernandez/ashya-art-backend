package com.ashyaart.ashya_art_backend.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.ClienteSolicitudCursoDto;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<CursoDto> getCursoById(@PathVariable Long id) {
        logger.info("getCursoById - Solicitud GET para obtener curso con ID: {}", id);
        CursoDto curso = cursoService.obtenerCursoPorId(id);

        if (curso != null) {
            logger.info("getCursoById - Curso encontrado con ID: {}", id);
            return ResponseEntity.ok(curso);
        } else {
            logger.warn("getCursoById - Curso no encontrado con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CursoDto> crearCurso(
        @RequestPart("curso") CursoDto cursoDto,
        @RequestPart(value = "imagenes", required = false) MultipartFile[] imagenes
    ) {
        logger.info("crearCurso - Solicitud POST para crear un nuevo curso: {}", cursoDto);

        try {
            if (imagenes != null) {
                if (imagenes.length > 5) {
                    return ResponseEntity.badRequest().body(null);
                }

                for (int i = 0; i < imagenes.length; i++) {
                    byte[] contenido = imagenes[i].getBytes();
                    switch (i) {
                        case 0 -> cursoDto.setImg1(contenido);
                        case 1 -> cursoDto.setImg2(contenido);
                        case 2 -> cursoDto.setImg3(contenido);
                        case 3 -> cursoDto.setImg4(contenido);
                        case 4 -> cursoDto.setImg5(contenido);
                    }
                }
            }

            CursoDto nuevoCurso = cursoService.crearCurso(cursoDto);
            logger.info("crearCurso - Curso creado con ID: {}", nuevoCurso.getId());
            return ResponseEntity.ok(nuevoCurso);

        } catch (IOException e) {
            logger.error("Error procesando las imágenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    
    @PostMapping("/solicitud-curso")
    public ResponseEntity<Void> solicitarCurso(@RequestBody ClienteSolicitudCursoDto solicitud) {
        logger.info("solicitarCurso - Solicitud de curso recibida: {}", solicitud);
        cursoService.solicitarCurso(solicitud);
        return ResponseEntity.ok().build();
    }

}
