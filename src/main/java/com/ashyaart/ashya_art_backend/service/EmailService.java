package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.Compra;

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
    
    public void enviarConfirmacionCompraTotal(String emailCliente, String nombreCliente, Compra compra) {

        String asunto = "Confirmation of your purchase on Ashya Art";

        String cuerpo = "Hello " + nombreCliente + ",\n\n" +
                        "Thank you for your purchase!\n\n" +
                        "💳 Purchase code: " + compra.getCodigoCompra() + "\n" +
                        "💶 Total: " + compra.getTotal() + " EUR\n\n" +
                        "We look forward to seeing you!\n\n" +
                        "Best regards,\n" +
                        "Ashya Art Team";

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(emailCliente);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mensaje.setFrom("ivangonzalez.code@gmail.com");

        mailSender.send(mensaje);
    }


    
    public void enviarConfirmacionCursoIndividual(String emailCliente, String nombreCliente,
            String nombreCurso, String fechaCurso,
            int plazasReservadas, BigDecimal bigDecimal,
            String informacionExtra) {

			String asunto = "Confirmation for your course - " + nombreCurso;
			
			String cuerpo = "Hello " + nombreCliente + ",\n\n" +
			"Thank you for your purchase! Here are the details of your course:\n\n" +
			"📘 Course: " + nombreCurso + "\n" +
			"📅 Date: " + fechaCurso + "\n" +
			"👥 Seats reserved: " + plazasReservadas + "\n" +
			"💶 Price: " + bigDecimal + " EUR\n" +
			(informacionExtra != null && !informacionExtra.isEmpty() 
			? "ℹ️ Additional info: " + informacionExtra + "\n" : "") +
			"\nWe look forward to seeing you in class!\n\n" +
			"Best regards,\n" +
			"Ashya Art Team";
			
			SimpleMailMessage mensaje = new SimpleMailMessage();
			mensaje.setTo(emailCliente);
			mensaje.setSubject(asunto);
			mensaje.setText(cuerpo);
			mensaje.setFrom("ivangonzalez.code@gmail.com");
			
			mailSender.send(mensaje);
	}


}
