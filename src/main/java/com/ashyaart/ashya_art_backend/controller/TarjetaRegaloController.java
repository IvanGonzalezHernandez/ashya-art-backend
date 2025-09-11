package com.ashyaart.ashya_art_backend.controller;

import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloFilter;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloDto;
import com.ashyaart.ashya_art_backend.model.ValidacionTarjetaDto;
import com.ashyaart.ashya_art_backend.service.TarjetaRegaloService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarjetas-regalo")
public class TarjetaRegaloController {

    private static final Logger logger = LoggerFactory.getLogger(TarjetaRegaloController.class);

    @Autowired
    private TarjetaRegaloService tarjetaRegaloService;

    // LISTADO CON FILTRO
    @GetMapping
    public ResponseEntity<List<TarjetaRegaloDto>> findByFilter(TarjetaRegaloFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener tarjetas regalo");
        List<TarjetaRegaloDto> tarjetasDto = tarjetaRegaloService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} tarjetas regalo", tarjetasDto.size());
        return ResponseEntity.ok(tarjetasDto);
    }

    // OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<TarjetaRegaloDto> getTarjetaRegaloById(@PathVariable Long id) {
        logger.info("getTarjetaRegaloById - Solicitud GET para obtener tarjeta regalo con ID: {}", id);
        TarjetaRegaloDto tarjeta = tarjetaRegaloService.obtenerTarjetaPorId(id);
        return (tarjeta != null) ? ResponseEntity.ok(tarjeta) : ResponseEntity.notFound().build();
    }

    // CREAR (MULTIPART)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TarjetaRegaloDto> crearTarjetaRegalo(
            @RequestPart("tarjeta") TarjetaRegaloDto tarjetaDto,
            @RequestPart(value = "img", required = false) MultipartFile img) {
        logger.info("crearTarjetaRegalo - POST (multipart)");
        try {
            if (img != null && !img.isEmpty()) {
                tarjetaDto.setImg(img.getBytes());
            }
            TarjetaRegaloDto nueva = tarjetaRegaloService.crearTarjetaRegalo(tarjetaDto);
            return ResponseEntity.ok(nueva);
        } catch (IOException e) {
            logger.error("crearTarjetaRegalo - Error leyendo imagen", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ACTUALIZAR (MULTIPART) + deleteImg como parte separada del FormData
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TarjetaRegaloDto> actualizarTarjetaRegalo(
            @PathVariable Long id,
            @RequestPart("tarjeta") TarjetaRegaloDto tarjetaDto,
            @RequestPart(value = "img", required = false) MultipartFile img,
            @RequestPart(value = "deleteImg", required = false) String deleteImg
    ) {
        logger.info("actualizarTarjetaRegalo - PUT (multipart) ID: {}", id);
        try {
            tarjetaDto.setId(id);
            byte[] nuevaImagen = null;
            if (img != null && !img.isEmpty()) {
                nuevaImagen = img.getBytes();
            }
            boolean mustDelete = deleteImg != null && "true".equalsIgnoreCase(deleteImg);
            TarjetaRegaloDto actualizada = tarjetaRegaloService.actualizarTarjetaRegalo(tarjetaDto, nuevaImagen, mustDelete);
            return ResponseEntity.ok(actualizada);
        } catch (IOException e) {
            logger.error("actualizarTarjetaRegalo - Error leyendo imagen", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // SERVIR IMAGEN
    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        TarjetaRegaloDto tarjeta = tarjetaRegaloService.obtenerTarjetaPorId(id);
        if (tarjeta == null || tarjeta.getImg() == null || tarjeta.getImg().length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // ajusta si guardas webp/png
                .body(tarjeta.getImg());
    }

    // ELIMINAR (borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarjetaRegalo(@PathVariable Long id) {
        tarjetaRegaloService.eliminarTarjetaRegalo(id);
        return ResponseEntity.noContent().build();
    }

    // VALIDAR CÓDIGO
    @PostMapping("/validar")
    public ResponseEntity<?> validarCodigo(@RequestBody ValidacionTarjetaDto request) {
        String codigo = request.getCodigo() == null ? "" : request.getCodigo().trim().toUpperCase();
        TarjetaRegaloCompra tarjeta = tarjetaRegaloService.obtenerTarjetaPorCodigo(codigo);

        if (tarjeta == null) return ResponseEntity.badRequest().body("Código inválido");
        if (!tarjeta.isEstado() || tarjeta.isCanjeada() || tarjeta.getFechaCaducidad().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body("Código inválido o ya canjeado");
        }
        return ResponseEntity.ok(tarjeta.getTarjetaRegalo().getPrecio());
    }
}
