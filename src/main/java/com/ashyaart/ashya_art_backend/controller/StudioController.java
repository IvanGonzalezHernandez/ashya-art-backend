package com.ashyaart.ashya_art_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ashyaart.ashya_art_backend.model.OpenStudioSolicitudDto;
import com.ashyaart.ashya_art_backend.service.StudioService;

@RestController
@RequestMapping("/api/studio")
public class StudioController {
	
	@Autowired
	StudioService studioService;
	
	private static final Logger logger = LoggerFactory.getLogger(FiringController.class);

  @PostMapping("/solicitud-studio")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Void> solicitarOpenStudio(@RequestBody OpenStudioSolicitudDto solicitud) {
  	logger.info("solicitarStudio - Solicitud POST para solicitar studio: {}", solicitud);
    studioService.solicitarStudio(solicitud);
    return ResponseEntity.ok().build();
  }
}
