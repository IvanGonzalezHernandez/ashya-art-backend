package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.Compra;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
    
    public void enviarConfirmacionProductoIndividual(String emailCliente, String nombreCliente,
            String nombreProducto, int cantidad, BigDecimal precioUnitario) {

			String asunto = "Confirmation for your product purchase - " + nombreProducto;
			
			BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
			
			String cuerpo = "Hello " + nombreCliente + ",\n\n" +
			"Thank you for your purchase! Here are the details of your product:\n\n" +
			"🛍️ Product: " + nombreProducto + "\n" +
			"👥 Quantity: " + cantidad + "\n" +
			"💶 Unit Price: " + precioUnitario + " EUR\n" +
			"💰 Total: " + total + " EUR\n\n" +
			"We hope you enjoy your product!\n\n" +
			"Best regards,\n" +
			"Ashya Art Team";
			
			SimpleMailMessage mensaje = new SimpleMailMessage();
			mensaje.setTo(emailCliente);
			mensaje.setSubject(asunto);
			mensaje.setText(cuerpo);
			mensaje.setFrom("ivangonzalez.code@gmail.com");
			
			mailSender.send(mensaje);
	}
    
    public void enviarConfirmacionSecretoIndividual(String emailCliente, String nombreCliente, String nombreSecreto, byte[] pdfBytes) {

			String asunto = "Your Secret Purchase - " + nombreSecreto;
			String cuerpo = "Hello " + nombreCliente + ",\n\n" +
			"Thank you for your purchase! Please find your secret attached as a PDF.\n\n" +
			"We hope you enjoy it!\n\n" +
			"Best regards,\n" +
			"Ashya Art Team";
			
			try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true); // true = multipart
			
			helper.setTo(emailCliente);
			helper.setSubject(asunto);
			helper.setText(cuerpo);
			helper.setFrom("ivangonzalez.code@gmail.com");
			
			// Adjuntar PDF
			ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);
			helper.addAttachment(nombreSecreto + ".pdf", pdfResource);
			
			mailSender.send(mensaje);
			
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new RuntimeException("Error sending email with attachment", e);
			}
	}
    
    public void enviarConfirmacionTarjetaRegaloIndividual(
            String destinatario,
            String codigo,
            String nombreCliente,
            String nombreReceptor,
            BigDecimal cantidad,
            LocalDate fechaExpiracion) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("🎁 Your AshYa Art Gift Card");

            String contenido = "<h2>Hello " + nombreReceptor + "!</h2>" +
                    "<p>You have received a gift card from <b>" + nombreCliente + "</b>.</p>" +
                    "<p><b>Gift Card Code:</b> " + codigo + "</p>" +
                    "<p><b>Amount:</b> €" + cantidad + "</p>" +
                    "<p>This gift card is valid until <b>" + fechaExpiracion + "</b> (6 months).</p>" +
                    "<p>You can redeem it in our online shop or courses at <a href='https://ashya-art.com'>Ashya Art</a>.</p>" +
                    "<br><p>Enjoy your gift! 🎨</p>";

            helper.setText(contenido, true);

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando la confirmación de la tarjeta regalo", e);
        }
    }




}
