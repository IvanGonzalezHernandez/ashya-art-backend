package com.ashyaart.ashya_art_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.model.FiringSolicitudDto;
import com.ashyaart.ashya_art_backend.service.FiringService;

@RestController
@RequestMapping("/api/firing")
public class FiringController {
	
	@Autowired
	private FiringService firingService;
	
	private static final Logger logger = LoggerFactory.getLogger(FiringController.class);
	
    @PostMapping("/solicitud-firing")
    public ResponseEntity<Void> solicitarFiring(@RequestBody FiringSolicitudDto solicitud) {
    	logger.info("solicitarFiring - Solicitud POST para solicitar firing: {}", solicitud);
        firingService.solicitarFiring(solicitud);
        return ResponseEntity.ok().build();
    }

}
