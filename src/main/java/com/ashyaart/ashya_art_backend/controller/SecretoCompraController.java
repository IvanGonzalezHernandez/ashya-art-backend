package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.filter.SecretoCompraFilter;
import com.ashyaart.ashya_art_backend.model.SecretoCompraDto;
import com.ashyaart.ashya_art_backend.service.SecretoCompraService;

@RestController
@RequestMapping("/api/secretos-compra")
public class SecretoCompraController {
	
	@Autowired
	private SecretoCompraService secretoCompraService;
	
	private static final Logger logger = LoggerFactory.getLogger(SecretoCompraController.class);
	
    @GetMapping
    public ResponseEntity<List<SecretoCompraDto>> findByFilter(SecretoCompraFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener compras de secretos");
        List<SecretoCompraDto> secretosCompraDto = secretoCompraService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} compras de secretos", secretosCompraDto.size());
        return ResponseEntity.ok(secretosCompraDto);
    }

}
