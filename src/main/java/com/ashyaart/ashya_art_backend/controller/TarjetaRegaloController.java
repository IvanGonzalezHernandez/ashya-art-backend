package com.ashyaart.ashya_art_backend.controller;

import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloFilter;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloDto;
import com.ashyaart.ashya_art_backend.service.TarjetaRegaloService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas-regalo")
public class TarjetaRegaloController {

    private static final Logger logger = LoggerFactory.getLogger(TarjetaRegaloController.class);

    @Autowired
    private TarjetaRegaloService tarjetaRegaloService;

    @GetMapping
    public ResponseEntity<List<TarjetaRegaloDto>> findByFilter(TarjetaRegaloFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener tarjetas regalo");
        List<TarjetaRegaloDto> tarjetasDto = tarjetaRegaloService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} tarjetas regalo", tarjetasDto.size());
        return ResponseEntity.ok(tarjetasDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TarjetaRegaloDto> getTarjetaRegaloById(@PathVariable Long id) {
        logger.info("getTarjetaRegaloById - Solicitud GET para obtener tarjeta regalo con ID: {}", id);
        TarjetaRegaloDto tarjeta = tarjetaRegaloService.obtenerTarjetaPorId(id);

        if (tarjeta != null) {
            logger.info("getTarjetaRegaloById - Tarjeta encontrada con ID: {}", id);
            return ResponseEntity.ok(tarjeta);
        } else {
            logger.warn("getTarjetaRegaloById - Tarjeta no encontrada con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<TarjetaRegaloDto> crearTarjetaRegalo(@RequestBody TarjetaRegaloDto tarjetaDto) {
        logger.info("crearTarjetaRegalo - Solicitud POST para crear tarjeta regalo: {}", tarjetaDto);
        TarjetaRegaloDto nueva = tarjetaRegaloService.crearTarjetaRegalo(tarjetaDto);
        logger.info("crearTarjetaRegalo - Tarjeta regalo creada con ID: {}", nueva.getId());
        return ResponseEntity.ok(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarjetaRegaloDto> actualizarTarjetaRegalo(@PathVariable Long id, @RequestBody TarjetaRegaloDto tarjetaDto) {
        logger.info("actualizarTarjetaRegalo - Solicitud PUT para actualizar tarjeta regalo con ID {}: {}", id, tarjetaDto);
        tarjetaDto.setId(id);
        TarjetaRegaloDto actualizada = tarjetaRegaloService.actualizarTarjetaRegalo(tarjetaDto);
        logger.info("actualizarTarjetaRegalo - Tarjeta regalo actualizada con ID: {}", actualizada.getId());
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarjetaRegalo(@PathVariable Long id) {
        logger.info("eliminarTarjetaRegalo - Solicitud DELETE para eliminar tarjeta regalo con ID: {}", id);
        tarjetaRegaloService.eliminarTarjetaRegalo(id);
        logger.info("eliminarTarjetaRegalo - Tarjeta regalo con ID {} eliminada (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
