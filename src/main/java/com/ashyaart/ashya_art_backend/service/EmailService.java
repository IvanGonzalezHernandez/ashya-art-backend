package com.ashyaart.ashya_art_backend.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.Compra;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

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
            String nombreCliente,
            String nombreReceptor,
            String codigo,
            BigDecimal cantidad,
            LocalDate fechaExpiracion
    ) {
        try {
            byte[] pdfBytes = generarTarjetaRegaloPdf(
                    codigo,
                    nombreReceptor,
                    cantidad,
                    nombreCliente,
                    fechaExpiracion
            );

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("🎁 Your Ashya Art Gift Card");

            String contenido = "<h2>Hello " + nombreReceptor + "!</h2>" +
                    "<p>You have received a gift card from <b>" + nombreCliente + "</b>.</p>" +
                    "<p><b>Gift Card Code:</b> " + codigo + "</p>" +
                    "<p><b>Amount:</b> €" + cantidad + "</p>" +
                    "<p>This gift card is valid until <b>" + fechaExpiracion + "</b> (6 months).</p>" +
                    "<p>You can redeem it in our online shop or courses at <a href='https://ashya-art.com'>Ashya Art</a>.</p>" +
                    "<br><p>Enjoy your gift! 🎨</p>";

            helper.setText(contenido, true);

            ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);
            helper.addAttachment("TarjetaRegalo_" + codigo + ".pdf", pdfResource);

            mailSender.send(mensaje);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando la confirmación de la tarjeta regalo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error generando la tarjeta regalo PDF", e);
        }
    }

    
    private byte[] generarTarjetaRegaloPdf(
            String codigo,
            String nombreReceptor,
            BigDecimal cantidad,
            String nombreCliente,
            LocalDate fechaExpiracion
    ) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Imagen base fija desde resources/assets
        ClassPathResource resource = new ClassPathResource("img/plantillaTarjetaRegalo.png");
        InputStream is = resource.getInputStream();
        byte[] imgBytes = is.readAllBytes();
        Image img = Image.getInstance(imgBytes);
        img.setAbsolutePosition(0, 0);
        img.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
        document.add(img);

        // Texto encima
        PdfContentByte canvas = writer.getDirectContent();
        canvas.beginText();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        canvas.setFontAndSize(bf, 24);
        canvas.setColorFill(BaseColor.BLACK);

        // Ajusta posiciones según tu diseño
        canvas.showTextAligned(Element.ALIGN_CENTER, "Gift Card", 300, 700, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "Code: " + codigo, 300, 650, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "To: " + nombreReceptor, 300, 600, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "From: " + nombreCliente, 300, 550, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "Amount: €" + cantidad, 300, 500, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "Valid until: " + fechaExpiracion, 300, 450, 0);

        canvas.endText();
        document.close();

        return baos.toByteArray();
    }


    
    




}
