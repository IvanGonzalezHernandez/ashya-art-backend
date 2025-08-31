package com.ashyaart.ashya_art_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.FiringSolicitudDto;

@Service
public class FiringService {
	
	@Autowired
	private EmailService emailService;
	
    public void solicitarFiring(FiringSolicitudDto solicitud) {
        emailService.enviarSolicitudFiring(
            solicitud.getNombre(),
            solicitud.getEmail(),
            solicitud.getTelefono(),
            solicitud.getTipoServicio(),
            solicitud.getNumeroPiezas(),
            solicitud.getDetallesMaterial(),
            solicitud.getPreguntasAdicionales()
        );
    }

}
