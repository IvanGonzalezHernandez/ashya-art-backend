package com.ashyaart.ashya_art_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailConfirmacion(String para, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mensaje.setFrom("ivangonzalez.code@gmail.com");

        mailSender.send(mensaje);
    }
    
    public void enviarSolicitudCursoAdmin(String nombreCliente, String apellidoCliente, String emailCliente, String telefonoCliente,
            String tipoClase, int personasInteresadas, String disponibilidad, String preguntasAdicionales) {

		String admin = "ivangonzalez.code@gmail.com";
		String asunto = "New Course Request - " + tipoClase;

		String cuerpo = "You have received a new course request:\n\n" +
		"Name: " + nombreCliente + " " + apellidoCliente + "\n" +
		"Email: " + emailCliente + "\n" +
		"Phone: " + telefonoCliente + "\n" +
		"Class type: " + tipoClase + "\n" +
		"People interested: " + personasInteresadas + "\n" +
		"Availability: " + disponibilidad + "\n" +
		"Additional questions: " + (preguntasAdicionales != null ? preguntasAdicionales : "None") + "\n\n" +
		"Please contact the client to coordinate the details.";
		
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setTo(admin);
		mensaje.setSubject(asunto);
		mensaje.setText(cuerpo);
		mensaje.setFrom("ivangonzalez.code@gmail.com");
		
		mailSender.send(mensaje);
	}

}
