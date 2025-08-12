package com.ashyaart.ashya_art_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.service.SecretoService;
import com.ashyaart.ashya_art_backend.filter.SecretoFilter;
import com.ashyaart.ashya_art_backend.model.ProductoDto;
import com.ashyaart.ashya_art_backend.model.SecretoDto;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/secretos")
public class SecretoController {

    private static final Logger logger = LoggerFactory.getLogger(SecretoController.class);

    @Autowired
    private SecretoService secretoService;

    @GetMapping
    public ResponseEntity<List<SecretoDto>> findByFilter(SecretoFilter filter) {
        logger.info("findAll - Solicitud GET para obtener todos los secretos");
        List<SecretoDto> lista = secretoService.findByFilter(filter);
        logger.info("findAll - Se encontraron {} secretos", lista.size());
        return ResponseEntity.ok(lista);
    }
    
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

    
}