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

import com.ashyaart.ashya_art_backend.filter.SecretoFilter;
import com.ashyaart.ashya_art_backend.model.SecretoDto;
import com.ashyaart.ashya_art_backend.service.SecretoService;

@RestController
@RequestMapping("/api/secretos")
public class SecretoController {

    private static final Logger logger = LoggerFactory.getLogger(SecretoController.class);

    @Autowired
    private SecretoService secretoService;

    // LISTADO / FILTRO
    @GetMapping
    public ResponseEntity<List<SecretoDto>> findByFilter(SecretoFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener secretos");
        List<SecretoDto> lista = secretoService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} secretos", lista.size());
        return ResponseEntity.ok(lista);
    }

    // OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<SecretoDto> getSecretoById(@PathVariable Long id) {
        logger.info("getSecretoById - Solicitud GET para obtener secreto con ID: {}", id);
        SecretoDto secreto = secretoService.obtenerSecretoPorId(id);
        if (secreto != null) {
            logger.info("getSecretoById - Secreto encontrado con ID: {}", id);
            return ResponseEntity.ok(secreto);
        } else {
            logger.warn("getSecretoById - Secreto no encontrado con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // CREAR (MULTIPART: secreto + img1..img5 + pdf)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SecretoDto> crearSecreto(
            @RequestPart("secreto") SecretoDto secretoDto,
            @RequestPart(value = "img1", required = false) MultipartFile img1,
            @RequestPart(value = "img2", required = false) MultipartFile img2,
            @RequestPart(value = "img3", required = false) MultipartFile img3,
            @RequestPart(value = "img4", required = false) MultipartFile img4,
            @RequestPart(value = "img5", required = false) MultipartFile img5,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf
    ) {
        try {
            if (img1 != null) secretoDto.setImg1(img1.getBytes());
            if (img2 != null) secretoDto.setImg2(img2.getBytes());
            if (img3 != null) secretoDto.setImg3(img3.getBytes());
            if (img4 != null) secretoDto.setImg4(img4.getBytes());
            if (img5 != null) secretoDto.setImg5(img5.getBytes());
            if (pdf  != null) secretoDto.setPdf(pdf.getBytes());

            SecretoDto nuevo = secretoService.crearSecreto(secretoDto);
            logger.info("crearSecreto - Secreto creado con ID: {}", nuevo.getId());
            return ResponseEntity.ok(nuevo);
        } catch (IOException e) {
            logger.error("crearSecreto - Error leyendo archivos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ACTUALIZAR (MULTIPART + flags de borrado)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SecretoDto> actualizarSecreto(
            @PathVariable Long id,
            @RequestPart("secreto") SecretoDto secretoDto,
            @RequestPart(value = "img1", required = false) MultipartFile img1,
            @RequestPart(value = "img2", required = false) MultipartFile img2,
            @RequestPart(value = "img3", required = false) MultipartFile img3,
            @RequestPart(value = "img4", required = false) MultipartFile img4,
            @RequestPart(value = "img5", required = false) MultipartFile img5,
            @RequestPart(value = "pdf",  required = false) MultipartFile pdf,
            @RequestParam(value = "deleteImg1", required = false, defaultValue = "false") boolean deleteImg1,
            @RequestParam(value = "deleteImg2", required = false, defaultValue = "false") boolean deleteImg2,
            @RequestParam(value = "deleteImg3", required = false, defaultValue = "false") boolean deleteImg3,
            @RequestParam(value = "deleteImg4", required = false, defaultValue = "false") boolean deleteImg4,
            @RequestParam(value = "deleteImg5", required = false, defaultValue = "false") boolean deleteImg5,
            @RequestParam(value = "deletePdf",  required = false, defaultValue = "false") boolean deletePdf
    ) throws IOException {
        logger.info("actualizarSecreto - Solicitud PUT para ID {}", id);
        secretoDto.setId(id);

        // Reemplazos (si llegan bytes)
        if (img1 != null) secretoDto.setImg1(img1.getBytes());
        if (img2 != null) secretoDto.setImg2(img2.getBytes());
        if (img3 != null) secretoDto.setImg3(img3.getBytes());
        if (img4 != null) secretoDto.setImg4(img4.getBytes());
        if (img5 != null) secretoDto.setImg5(img5.getBytes());
        if (pdf  != null) secretoDto.setPdf(pdf.getBytes());

        // Flags de borrado
        secretoDto.setDeleteImg1(deleteImg1);
        secretoDto.setDeleteImg2(deleteImg2);
        secretoDto.setDeleteImg3(deleteImg3);
        secretoDto.setDeleteImg4(deleteImg4);
        secretoDto.setDeleteImg5(deleteImg5);
        secretoDto.setDeletePdf(deletePdf);

        SecretoDto actualizado = secretoService.actualizarSecreto(secretoDto);
        logger.info("actualizarSecreto - Secreto actualizado ID {}", id);
        return ResponseEntity.ok(actualizado);
    }

    // OBTENER IMAGEN POR SLOT
    @GetMapping("/{id}/imagen/{slot}")
    public ResponseEntity<byte[]> obtenerImagen(
            @PathVariable Long id,
            @PathVariable int slot
    ) {
        SecretoDto secreto = secretoService.obtenerSecretoPorId(id);
        if (secreto == null) return ResponseEntity.notFound().build();

        byte[] data = switch (slot) {
            case 1 -> secreto.getImg1();
            case 2 -> secreto.getImg2();
            case 3 -> secreto.getImg3();
            case 4 -> secreto.getImg4();
            case 5 -> secreto.getImg5();
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

    // OBTENER PDF
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> obtenerPdf(@PathVariable Long id) {
        SecretoDto secreto = secretoService.obtenerSecretoPorId(id);
        if (secreto == null || secreto.getPdf() == null || secreto.getPdf().length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(secreto.getPdf());
    }

    // ELIMINAR (borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSecreto(@PathVariable Long id) {
        logger.info("eliminarSecreto - Solicitud DELETE para ID: {}", id);
        secretoService.eliminarSecreto(id);
        logger.info("eliminarSecreto - Secreto con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
