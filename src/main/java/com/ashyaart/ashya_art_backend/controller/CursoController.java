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
            @RequestPart(value = "img1", required = false) MultipartFile img1,
            @RequestPart(value = "img2", required = false) MultipartFile img2,
            @RequestPart(value = "img3", required = false) MultipartFile img3,
            @RequestPart(value = "img4", required = false) MultipartFile img4,
            @RequestPart(value = "img5", required = false) MultipartFile img5
    ) {
        try {
            if (img1 != null) cursoDto.setImg1(img1.getBytes());
            if (img2 != null) cursoDto.setImg2(img2.getBytes());
            if (img3 != null) cursoDto.setImg3(img3.getBytes());
            if (img4 != null) cursoDto.setImg4(img4.getBytes());
            if (img5 != null) cursoDto.setImg5(img5.getBytes());

            CursoDto nuevo = cursoService.crearCurso(cursoDto);
            return ResponseEntity.ok(nuevo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CursoDto> actualizarCurso(
            @PathVariable Long id,
            @RequestPart("curso") CursoDto cursoDto,
            @RequestPart(value = "img1", required = false) MultipartFile img1,
            @RequestPart(value = "img2", required = false) MultipartFile img2,
            @RequestPart(value = "img3", required = false) MultipartFile img3,
            @RequestPart(value = "img4", required = false) MultipartFile img4,
            @RequestPart(value = "img5", required = false) MultipartFile img5,
            @RequestParam(value = "deleteImg1", required = false, defaultValue = "false") boolean deleteImg1,
            @RequestParam(value = "deleteImg2", required = false, defaultValue = "false") boolean deleteImg2,
            @RequestParam(value = "deleteImg3", required = false, defaultValue = "false") boolean deleteImg3,
            @RequestParam(value = "deleteImg4", required = false, defaultValue = "false") boolean deleteImg4,
            @RequestParam(value = "deleteImg5", required = false, defaultValue = "false") boolean deleteImg5
    ) throws IOException {
        cursoDto.setId(id);

        // pasar los bytes al DTO sólo si hay reemplazo
        if (img1 != null) cursoDto.setImg1(img1.getBytes());
        if (img2 != null) cursoDto.setImg2(img2.getBytes());
        if (img3 != null) cursoDto.setImg3(img3.getBytes());
        if (img4 != null) cursoDto.setImg4(img4.getBytes());
        if (img5 != null) cursoDto.setImg5(img5.getBytes());

        // flags de borrado
        cursoDto.setDeleteImg1(deleteImg1);
        cursoDto.setDeleteImg2(deleteImg2);
        cursoDto.setDeleteImg3(deleteImg3);
        cursoDto.setDeleteImg4(deleteImg4);
        cursoDto.setDeleteImg5(deleteImg5);

        CursoDto actualizado = cursoService.actualizarCurso(cursoDto);
        return ResponseEntity.ok(actualizado);
    }

    
    @GetMapping("/{id}/imagen/{slot}")
    public ResponseEntity<byte[]> obtenerImagen(
            @PathVariable Long id,
            @PathVariable int slot
    ) {
        CursoDto curso = cursoService.obtenerCursoPorId(id);
        if (curso == null) return ResponseEntity.notFound().build();

        byte[] data = switch (slot) {
            case 1 -> curso.getImg1();
            case 2 -> curso.getImg2();
            case 3 -> curso.getImg3();
            case 4 -> curso.getImg4();
            case 5 -> curso.getImg5();
            default -> null;
        };

        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("image/webp"))
                .body(data);
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
