package com.ashyaart.ashya_art_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.OpenStudioSolicitudDto;

@Service
public class StudioService {
	
	@Autowired
	private EmailService emailService;
	
  public void solicitarStudio(OpenStudioSolicitudDto solicitud) {

	    emailService.enviarSolicitudStudio(
	        solicitud.getNombre(),
	        solicitud.getEmail(),
	        solicitud.getTelefono(),
	        solicitud.getOption(),
	        solicitud.getPreguntasAdicionales()
	    );
	  }

}
