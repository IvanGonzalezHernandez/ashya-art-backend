package com.ashyaart.ashya_art_backend.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ashyaart.ashya_art_backend.entity.Compra;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraNoStripeAdminEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraStripeAdminErrorEvent;
import com.ashyaart.ashya_art_backend.event.CompraEventos.CompraStripeAdminSuccessEvent;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);
  //Rate limit sencillo para Resend (máx. ~1 email cada 700 ms)
  private final Object rateLimitLock = new Object();
  private long lastEmailTimestamp = 0L;
  
  private final RestTemplate http = new RestTemplate();
  @Value("${resend.api.key:}")
  private String apiKey;

  @Value("${resend.from:}")
  private String from;
  
  @Value("${resend.reply-to:}")
  private String replyTo;
  
  @Value("${mail.admin.to}")
  private String adminTo;
  
  @Value("${entorno.nombre:LOCAL}")
  private String entornoNombre;

  public EmailService(
      @Value("${resend.api.key}") String apiKey,
      @Value("${resend.from}") String from
  ) {
    this.apiKey = apiKey;
    this.from = from;
  }

  /* ===================== Base (TEXT / HTML / HTML+Adjunto) ===================== */

  private void sendText(String to, String subject, String text) {
    log.info("[Resend] TEXT -> to='{}' subject='{}'", to, subject);
    Map<String, Object> body = Map.of(
        "from", from,
        "to", List.of(to),
        "subject", subject,
        "text", text
    );
    
    applyRateLimit();
    
    post(body);
  }

  private void sendHtml(String to, String subject, String html) {
    log.info("[Resend] HTML -> to='{}' subject='{}'", to, subject);
    Map<String, Object> body = Map.of(
        "from", from,
        "to", List.of(to),
        "subject", subject,
        "html", html
    );
    
    applyRateLimit();
    
    post(body);
  }

  private void sendHtmlWithAttachment(String to, String subject, String html,
                                      String filename, byte[] data) {
    log.info("[Resend] HTML+ATTACHMENT -> to='{}' subject='{}' file='{}' ({} bytes)",
        to, subject, filename, data != null ? data.length : 0);

    Map<String, Object> attachment = Map.of(
        "filename", filename,
        "content", Base64.getEncoder().encodeToString(data) // Resend espera base64
    );

    Map<String, Object> body = Map.of(
        "from", from,
        "to", List.of(to),
        "subject", subject,
        "html", html,
        "attachments", List.of(attachment)
    );
    
    applyRateLimit();
    
    post(body);
  }

  private void post(Map<String, Object> body) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(apiKey);
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("Idempotency-Key", UUID.randomUUID().toString());

      RequestEntity<Map<String, Object>> req = RequestEntity
          .post("https://api.resend.com/emails")
          .headers(headers)
          .body(body);

      ResponseEntity<String> resp =
          http.exchange(req, new ParameterizedTypeReference<>() {});
      log.info("[Resend] OK -> status={} body={}", resp.getStatusCode(), resp.getBody());
    } catch (Exception ex) {
      log.error("[Resend] ERROR enviando email", ex);
      throw new RuntimeException("Error enviando email con Resend", ex);
    }
  }
  
  /**
   * Aplica un pequeño retardo entre envíos para no superar
   * el límite de 2 peticiones/segundo de Resend.
   */
  private void applyRateLimit() {
    synchronized (rateLimitLock) {
      long now = System.currentTimeMillis();
      long minIntervalMs = 2000; // 2 segundos

      long wait = lastEmailTimestamp + minIntervalMs - now;
      if (wait > 0) {
        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        now = System.currentTimeMillis();
      }

      lastEmailTimestamp = now;
    }
  }


  /* ===================== Métodos públicos ===================== */

  public void enviarEmailConfirmacion(String para, String asunto, String cuerpo) {
    // mismo contenido que antes (texto plano)
    sendText(para, asunto, cuerpo);
  }
  


  private String resolveBaseUrl() {
      String entorno = entornoNombre != null ? entornoNombre.trim().toUpperCase() : "LOCAL";

      if ("PRO".equals(entorno)) {
          return "https://ashya-art-backend-pro.onrender.com";
      }

      return "http://localhost:8080";
  }
  
  public void enviarConfirmacionNewsletter(String destinatario) {
      String asunto = "📰 Newsletter Subscription Confirmation";

      // 1) Selección automática según entorno
      String baseUrl = resolveBaseUrl();

      // 2) Codificar email
      String encodedEmail = URLEncoder.encode(destinatario, StandardCharsets.UTF_8);
      String unsubscribeUrl = baseUrl + "/api/newsletters/unsubscribe?email=" + encodedEmail;

      // 3) Email HTML
      String contenidoHtml =
          "<html>" +
            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
              "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
                "<h2 style='color:#333; margin-top:0;'>Welcome to Ashya Art!</h2>" +
                "<p>Thank you for subscribing to our <b>newsletter</b>.</p>" +
                "<p>You'll now receive updates about our upcoming workshops, new ceramic collections, and exclusive offers.</p>" +
                "<p>We’re excited to have you as part of our creative community 💫</p>" +

                "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +

                "<p>If you’d like to stay more connected, feel free to follow us:</p>" +
                "<ul style='line-height:1.7; padding-left:20px;'>" +
                  "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8;' target='_blank'>@ashya_art</a></li>" +
                  "<li>🌐 Website: <a href='https://ashya-art.com' style='color:#1a73e8;' target='_blank'>Ashya Art</a></li>" +
                  "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8;'>ashyaxart@gmail.com</a></li>" +
                "</ul>" +

                "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +

                "<p style='font-size:12px; color:#777;'>If you no longer want to receive our newsletters, you can " +
                  "<a href='" + unsubscribeUrl + "' style='color:#1a73e8;'>unsubscribe here</a>." +
                "</p>" +

                "<p style='margin-top:24px;'>Best regards,<br><b>Ashya Art Team</b></p>" +
              "</div>" +
            "</body>" +
          "</html>";

      sendHtml(destinatario, asunto, contenidoHtml);
  }

  
  public void enviarConfirmacionSolicitudCursoCliente(
		    String nombreCliente,
		    String tipoClase,
		    String emailCliente
		) {
		  String asunto = "🎨 Course Request Confirmation - " + tipoClase;

		  String contenido =
		      "<html>" +
		        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
		            "<h2 style='color:#333; margin-top:0;'>🎨 Course Request Confirmation</h2>" +
		            "<p>Hello <b>" + nombreCliente + "</b>,</p>" +
		            "<p>We have received your request for the course: <b>" + tipoClase + "</b>.</p>" +
		            "<p>We will contact you soon to coordinate the details and confirm the schedule.</p>" +
		            "<p>We’re excited to have you in one of our workshops!</p>" +
		            "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
		            "<p>If you need any assistance or have more questions, you can contact me through:</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
		              "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
		              "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
		              "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
		            "</ul>" +
		            "<p style='margin-top:24px;'>Best regards,<br><b>Ashya</b></p>" +
		          "</div>" +
		        "</body>" +
		      "</html>";

		  sendHtml(emailCliente, asunto, contenido);
		}


  public void enviarSolicitudCursoAdmin(
		    String nombreCliente,
		    String apellidoCliente,
		    String emailCliente,
		    String telefonoCliente,
		    String tipoClase,
		    int personasInteresadas,
		    String disponibilidad,
		    String preguntasAdicionales
		) {
		  String admin = adminTo;
		  String asunto = "🎨 New Course Request - " + tipoClase;

		  String preguntas = (preguntasAdicionales != null && !preguntasAdicionales.isBlank()) ? preguntasAdicionales : "None";
		  String nombreCompleto = (nombreCliente != null ? nombreCliente : "") + " " + (apellidoCliente != null ? apellidoCliente : "");

		  String contenido =
		      "<html>" +
		        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
		            "<h2 style='color:#333; margin-top:0;'>🎨 New Course Request</h2>" +
		            "<p>You have received a new course request. Details below:</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li><b>Name:</b> " + nombreCompleto.trim() + "</li>" +
		              "<li><b>Email:</b> <a href='mailto:" + emailCliente + "' style='color:#1a73e8; text-decoration:none;'>" + emailCliente + "</a></li>" +
		              "<li><b>Phone:</b> <a href='tel:" + telefonoCliente + "' style='color:#1a73e8; text-decoration:none;'>" + telefonoCliente + "</a></li>" +
		              "<li><b>Class type:</b> " + tipoClase + "</li>" +
		              "<li><b>People interested:</b> " + personasInteresadas + "</li>" +
		              "<li><b>Availability:</b> " + disponibilidad + "</li>" +
		              "<li><b>Additional questions:</b> " + preguntas + "</li>" +
		            "</ul>" +
		            "<p>Please contact the client to coordinate the details.</p>" +
		          "</div>" +
		        "</body>" +
		      "</html>";

		  sendHtml(admin, asunto, contenido);
		}


  public void enviarConfirmacionCompraTotal(String emailCliente, String nombreCliente, Compra compra) {
	  boolean pagado = Boolean.TRUE.equals(compra.getPagado());

	  String asunto;
	  String titulo;
	  String parrafoIntro;
	  String parrafoExtra;

	  if (pagado) {
	    // COMPRA NORMAL (Stripe o NoStripe 100%)
	    asunto = "🛍️ Confirmation of your purchase on Ashya Art";
	    titulo = "✅ Purchase Confirmation";
	    parrafoIntro = "<p>Thank you for your purchase!</p>";
	    parrafoExtra = "<p>We look forward to seeing you!</p>";
	  } else {
	    // RESERVA ATELIER (pago en el estudio)
	    asunto = "🧾 Reservation confirmed – payment at the Atelier";
	    titulo = "🧾 Atelier Reservation Confirmation";
	    parrafoIntro =
	        "<p>Thank you for your reservation!</p>" +
	        "<p><b>You will pay the total amount at the Atelier on the day of the course / pickup.</b></p>";
	    parrafoExtra =
	        "<p>If you need to change or cancel your reservation, please contact me in advance.</p>";
	  }

	  String contenido =
	      "<html>" +
	        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
	          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
	            "<h2 style='color:#333; margin-top:0;'>" + titulo + "</h2>" +
	            "<p>Hello <b>" + nombreCliente + "</b>,</p>" +
	             parrafoIntro +
	            "<ul style='line-height:1.7; padding-left:20px;'>" +
	              "<li><b>💳 Purchase code:</b> " + compra.getCodigoCompra() + "</li>" +
	              "<li><b>💶 Total:</b> " + compra.getTotal() + " EUR</li>" +
	            "</ul>" +
	             parrafoExtra +
	            "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
	            "<p>If you have any questions about your purchase or reservation, you can contact me through any of the following options:</p>" +
	            "<ul style='line-height:1.7; padding-left:20px;'>" +
	              "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
	              "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
	              "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
	              "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
	            "</ul>" +
	            "<p style='margin-top:24px;'>Best regards,<br><b>Ashya Art Team</b></p>" +
	          "</div>" +
	        "</body>" +
	      "</html>";

	  sendHtml(emailCliente, asunto, contenido);
	}


  public void enviarConfirmacionCursoIndividual(String emailCliente, String nombreCliente,
                                                String nombreCurso, String fechaCurso, String horaCurso,
                                                int plazasReservadas, BigDecimal bigDecimal,
                                                String informacionExtra) {
	String asunto = "🎨 Confirmation for your course - " + nombreCurso;

    String direccionTexto = "Ashya Art & Keramik Studio, Pinneberger Ch 74, 22523 Hamburg";
    String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" +
        URLEncoder.encode(direccionTexto, StandardCharsets.UTF_8);

    String cuerpoHtml =
        "<html>" +
            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
            "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
            "<h2 style='color:#333;'>Dear " + nombreCliente + ",</h2>" +
            "<p>Thank you for purchasing the <b>" + nombreCurso + "</b> from Ashya Art.</p>" +
            "<p>The course will start at <b>" + horaCurso + "</b>.<br>" +
            "We kindly ask you to arrive <b>10–15 minutes before</b> the scheduled time.</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li><b>📘 Course:</b> " + nombreCurso + "</li>" +
            "<li><b>📅 Date:</b> " + fechaCurso + "</li>" +
            "<li><b>👥 Seats reserved:</b> " + plazasReservadas + "</li>" +
            "<li><b>💶 Price:</b> " + bigDecimal + " EUR</li>" +
            "<li><b>📍 Address:</b> " +
            "<a href='" + mapsUrl + "' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>" +
            direccionTexto + "</a></li>" +
            "</ul>" +
            "<p>If you have any trouble finding the studio or if you’d like to share your creative ideas in advance, you can contact me through any of the following options:</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
            "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
            "</ul>" +
            "<p>Best regards,<br><b>Ashya</b></p>" +
            "</div>" +
            "</body></html>";

    sendHtml(emailCliente, asunto, cuerpoHtml);
  }

  public void enviarConfirmacionProductoIndividual(String emailCliente,
                                                   String nombreCliente,
                                                   String nombreProducto,
                                                   int cantidad,
                                                   BigDecimal precioUnitario) {
    String asunto = "🏺 Confirmation for your product purchase - " + nombreProducto;

    String cuerpoHtml =
        "<html>" +
            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
            "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
            "<h2 style='color:#333;'>Dear " + nombreCliente + ",</h2>" +
            "<p>Thank you for purchasing ceramic art from <b>Ashya Art</b>.</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li><b>🛍️ Product:</b> " + nombreProducto + "</li>" +
            "<li><b>👥 Quantity:</b> " + cantidad + "</li>" +
            "</ul>" +
            "<p>Soon I will provide your delivery number so you can track your purchase.</p>" +
            "<p>If you have any questions about your purchase, you can contact me through any of the following options:</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
            "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
            "</ul>" +
            "<p>Best regards,<br><b>Ashya</b></p>" +
            "</div>" +
            "</body></html>";

    sendHtml(emailCliente, asunto, cuerpoHtml);
  }

  public void enviarConfirmacionSecretoIndividual(String emailCliente,
                                                  String nombreCliente,
                                                  String nombreSecreto,
                                                  byte[] pdfBytes) {
    String asunto = "🔐 Your Secret Purchase - " + nombreSecreto;

    String cuerpoHtml =
        "<html>" +
            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
            "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
            "<h2 style='color:#333;'>Dear " + nombreCliente + ",</h2>" +
            "<p>Thank you for your purchase from <b>Ashya Art</b>. Please find attached your PDF document containing the information/instruction you have purchased.</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li><b>🔐 Secret:</b> " + nombreSecreto + "</li>" +
            "</ul>" +
            "<p>If the attachment doesn’t open on your device, feel free to contact me using the details below and I’ll resend it.</p>" +
            "<p>If you have any questions, you can contact me through any of the following options:</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
            "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
            "</ul>" +
            "<p>Best regards,<br><b>Ashya</b></p>" +
            "</div>" +
            "</body></html>";

    sendHtmlWithAttachment(emailCliente, asunto, cuerpoHtml, nombreSecreto + ".pdf", pdfBytes);
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
		        codigo, nombreReceptor, cantidad, nombreCliente, fechaExpiracion
		    );

		    String asunto = "🎁 Your Ashya Art Gift Card";

		    String contenido =
		        "<html>" +
		            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		            "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +

		            "<h2 style='color:#333; margin-top:0;'>🎁 Your Ashya Art Gift Card</h2>" +
		            "<p>Hello <b>" + nombreCliente + "</b>!</p>" +
		            "<p>You have bought a gift card for <b>" + nombreReceptor + "</b>.</p>" +

		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		            "<li><b>💳 Gift Card Code:</b> " + codigo + "</li>" +
		            "<li><b>💶 Amount:</b> €" + cantidad + "</li>" +
		            "<li><b>📅 Valid until:</b> " + fechaExpiracion + " (6 months)</li>" +
		            "</ul>" +

		            "<p>You can redeem it in our online shop or courses at " +
		            "<a href='https://ashya-art.com' style='color:#1a73e8; text-decoration:none;'>Ashya Art</a>.</p>" +

		            "<p>If the attachment doesn’t open on your device, just reply to this email and I’ll resend it.</p>" +

		            "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +

		            "<p>If you have any questions, you can contact me through any of the following options:</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		            "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
		            "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
		            "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
		            "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
		            "</ul>" +

		            "<p style='margin-top:24px;'>Best regards,<br><b>Ashya</b></p>" +
		            "</div>" +
		            "</body></html>";

		    sendHtmlWithAttachment(destinatario, asunto, contenido, "GiftCard_" + codigo + ".pdf", pdfBytes);

		  } catch (Exception e) {
		    throw new RuntimeException("Error generando la tarjeta regalo PDF", e);
		  }
		}


  public void enviarSolicitudFiring(
		    String nombreCliente,
		    String emailCliente,
		    String telefonoCliente,
		    String tipoServicio,
		    int numeroPiezas,
		    String detallesMaterial,
		    String preguntasAdicionales
		) {

		  String preguntas = (preguntasAdicionales != null && !preguntasAdicionales.isBlank())
		      ? preguntasAdicionales
		      : "None";
		  String detalles  = (detallesMaterial != null && !detallesMaterial.isBlank())
		      ? detallesMaterial
		      : "—";

		  // ====== Cliente  ======
		  String asuntoCliente = "🔥 Firing Service Request Confirmation";
		  String contenidoCliente =
		      "<html>" +
		        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
		            "<h2 style='color:#333; margin-top:0;'>🔥 Firing Service Request Received</h2>" +
		            "<p>Hello <b>" + nombreCliente + "</b>,</p>" +
		            "<p>We have received your request for the firing service: <b>" + mapTipoServicio(tipoServicio) + "</b>.</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li><b>Number of pieces:</b> " + numeroPiezas + "</li>" +
		              "<li><b>Clay / glaze details:</b> " + detalles + "</li>" +
		              "<li><b>Additional questions:</b> " + preguntas + "</li>" +
		            "</ul>" +
		            "<p>We will contact you soon to coordinate the details and scheduling.</p>" +
		            "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
		            "<p>If you need anything in the meantime, you can reach me here:</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
		              "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
		              "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com' style='color:#1a73e8; text-decoration:none;'>ashyaxart@gmail.com</a></li>" +
		              "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' style='color:#1a73e8; text-decoration:none;' target='_blank' rel='noopener noreferrer'>@ashya_art</a></li>" +
		            "</ul>" +
		            "<p style='margin-top:24px;'>Best regards,<br><b>Ashya</b></p>" +
		          "</div>" +
		        "</body>" +
		      "</html>";

		  sendHtml(emailCliente, asuntoCliente, contenidoCliente);

		  // ====== Admin ======
		  String admin = adminTo;
		  String asuntoAdmin = "🔥 New Firing Service Request - " + mapTipoServicio(tipoServicio);

		  String contenidoAdmin =
		      "<html>" +
		        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
		            "<h2 style='color:#333; margin-top:0;'>🧱 New Firing Service Request</h2>" +
		            "<p>You have received a new firing service request. Details below:</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li><b>Name:</b> " + nombreCliente + "</li>" +
		              "<li><b>Email:</b> <a href='mailto:" + emailCliente + "' style='color:#1a73e8; text-decoration:none;'>" + emailCliente + "</a></li>" +
		              "<li><b>Phone:</b> <a href='tel:" + telefonoCliente + "' style='color:#1a73e8; text-decoration:none;'>" + telefonoCliente + "</a></li>" +
		              "<li><b>Service type:</b> " + mapTipoServicio(tipoServicio) + "</li>" +
		              "<li><b>Number of pieces:</b> " + numeroPiezas + "</li>" +
		              "<li><b>Clay / glaze details:</b> " + detalles + "</li>" +
		              "<li><b>Additional questions:</b> " + preguntas + "</li>" +
		            "</ul>" +
		            "<p>Please contact the client to coordinate the details.</p>" +
		          "</div>" +
		        "</body>" +
		      "</html>";

		  sendHtml(admin, asuntoAdmin, contenidoAdmin);
		}

  private String mapTipoServicio(String tipoServicio) {
    return switch (tipoServicio) {
      case "entireKilnBisque" -> "Rent entire kiln – Bisque firing (35€)";
      case "entireKilnGlaze" -> "Rent entire kiln – Glaze firing (40€)";
      case "singlePiece" -> "Single piece firing (4€)";
      default -> tipoServicio;
    };
  }
  
  public void enviarSolicitudStudio(
		    String nombreCliente,
		    String emailCliente,
		    String telefonoCliente,
		    String option,
		    String preguntasAdicionales
		) {

		  String preguntas = (preguntasAdicionales != null && !preguntasAdicionales.isBlank())
		      ? preguntasAdicionales
		      : "None";

		  String opcionTexto = mapStudioOption(option);

		  // ====== CLIENTE ======
		  String asuntoCliente = "🎨 Open Studio Request Received";

		  String contenidoCliente =
		      "<html>" +
		        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
		            "<h2 style='margin-top:0;'>🎨 Open Studio Request Received</h2>" +
		            "<p>Hello <b>" + nombreCliente + "</b>,</p>" +
		            "<p>Thank you for your interest in joining the <b>Open Studio</b>.</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li><b>Selected option:</b> " + opcionTexto + "</li>" +
		              "<li><b>Additional message:</b> " + preguntas + "</li>" +
		            "</ul>" +
		            "<p>I will get back to you shortly to confirm availability and details.</p>" +
		            "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
		            "<p>If you need anything in the meantime, feel free to contact me:</p>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li>📞 Phone: <a href='tel:+491638681397'>+49 163 8681397</a></li>" +
		              "<li>📧 Email: <a href='mailto:ashyaxart@gmail.com'>ashyaxart@gmail.com</a></li>" +
		              "<li>📷 Instagram: <a href='https://www.instagram.com/ashya_art' target='_blank'>@ashya_art</a></li>" +
		            "</ul>" +
		            "<p style='margin-top:24px;'>Best regards,<br><b>Ashya</b></p>" +
		          "</div>" +
		        "</body>" +
		      "</html>";

		  sendHtml(emailCliente, asuntoCliente, contenidoCliente);

		  // ====== ADMIN ======
		  String asuntoAdmin = "🎨 New Open Studio Request";

		  String contenidoAdmin =
		      "<html>" +
		        "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
		          "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
		            "<h2 style='margin-top:0;'>🎨 New Open Studio Request</h2>" +
		            "<ul style='line-height:1.7; padding-left:20px;'>" +
		              "<li><b>Name:</b> " + nombreCliente + "</li>" +
		              "<li><b>Email:</b> <a href='mailto:" + emailCliente + "'>" + emailCliente + "</a></li>" +
		              "<li><b>Phone:</b> <a href='tel:" + telefonoCliente + "'>" + telefonoCliente + "</a></li>" +
		              "<li><b>Selected option:</b> " + opcionTexto + "</li>" +
		              "<li><b>Message:</b> " + preguntas + "</li>" +
		            "</ul>" +
		            "<p>Please contact the client to continue the process.</p>" +
		          "</div>" +
		        "</body>" +
		      "</html>";

		  sendHtml(adminTo, asuntoAdmin, contenidoAdmin);
		}
  
	  private String mapStudioOption(String option) {
		  return switch (option) {
		    case "1_time_entry" -> "1 time entry — 30€ / person";
		    case "10_times_entry" -> "10 times entry — 200€ / person";
		    case "unlimited_entry" -> "Unlimited entry — 50€ / month";
		    default -> option;
		  };
		}


  
  // ======================
  // Notificaciones ADMIN – STRIPE
  // ======================

  public void enviarNotificacionAdminCompraStripe(CompraStripeAdminSuccessEvent event) {
	    String adminEmail = adminTo;

	    String flujo = "STRIPE_CHECKOUT";
	    String estadoEmoji = "✅";
	    String estadoTexto = "SUCCESS";

	    String asunto = estadoEmoji + " Stripe checkout – " + estadoTexto;

	    // Detalles del carrito (igual estilo que NoStripe)
	    StringBuilder detallesCarrito = new StringBuilder();
	    if (event.carrito() != null && event.carrito().getItems() != null) {
	        event.carrito().getItems().forEach(item -> {
	            detallesCarrito
	                .append("<li>")
	                .append("<b>Type:</b> ").append(item.getTipo()).append(" | ")
	                .append("<b>Name:</b> ").append(item.getNombre()).append(" | ")
	                .append("<b>Qty:</b> ").append(item.getCantidad()).append(" | ")
	                .append("<b>Price:</b> ").append(item.getPrecio()).append(" EUR")
	                .append("</li>");
	        });
	    }

	    String nombreCliente = event.nombreCliente() != null ? event.nombreCliente() : "-";
	    String emailCliente  = event.emailCliente() != null ? event.emailCliente() : "-";

	    String codigoCompra  = (event.compra() != null) ? event.compra().getCodigoCompra() : "-";
	    String totalCompra   = (event.compra() != null && event.compra().getTotal() != null)
	            ? event.compra().getTotal().toString()
	            : "-";
	    String pagado        = (event.compra() != null) ? String.valueOf(event.compra().getPagado()) : "-";
	    String fechaCompra   = (event.compra() != null && event.compra().getFechaCompra() != null)
	            ? event.compra().getFechaCompra().toString()
	            : "-";

	    String contenidoHtml =
	        "<html>" +
	          "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
	            "<div style='max-width:700px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +

	              "<h2 style='color:#333; margin-top:0;'>" + estadoEmoji + " Stripe checkout (" + estadoTexto + ")</h2>" +

	              "<p><b>Flow:</b> " + flujo + "</p>" +

	              "<h3 style='margin-top:24px;'>Client</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                "<li><b>Name:</b> " + nombreCliente + "</li>" +
	                "<li><b>Email:</b> " + emailCliente + "</li>" +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Order</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                "<li><b>Code:</b> " + codigoCompra + "</li>" +
	                "<li><b>Total:</b> " + totalCompra + " EUR</li>" +
	                "<li><b>Paid flag:</b> " + pagado + "</li>" +
	                "<li><b>Date:</b> " + fechaCompra + "</li>" +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Cart items</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                (detallesCarrito.length() > 0 ? detallesCarrito.toString() : "<li>No items</li>") +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Error message</h3>" +
	              "<p>-</p>" +

	              "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
	              "<p style='font-size:12px; color:#777;'>This email was generated automatically from the Stripe checkout flow.</p>" +

	            "</div>" +
	          "</body>" +
	        "</html>";

	    sendHtml(adminEmail, asunto, contenidoHtml);
	}



  public void enviarNotificacionAdminCompraStripeError(CompraStripeAdminErrorEvent event) {
	    String adminEmail = adminTo;

	    String flujo = "STRIPE_CHECKOUT";
	    String estadoEmoji = "❌";
	    String estadoTexto = "ERROR";

	    String asunto = estadoEmoji + " Stripe checkout – " + estadoTexto;

	    // Cart items también aquí
	    StringBuilder detallesCarrito = new StringBuilder();
	    if (event.carrito() != null && event.carrito().getItems() != null) {
	        event.carrito().getItems().forEach(item -> {
	            detallesCarrito
	                .append("<li>")
	                .append("<b>Type:</b> ").append(item.getTipo()).append(" | ")
	                .append("<b>Name:</b> ").append(item.getNombre()).append(" | ")
	                .append("<b>Qty:</b> ").append(item.getCantidad()).append(" | ")
	                .append("<b>Price:</b> ").append(item.getPrecio()).append(" EUR")
	                .append("</li>");
	        });
	    }

	    String nombreCliente = event.nombreCliente() != null ? event.nombreCliente() : "-";
	    String emailCliente  = event.emailCliente() != null ? event.emailCliente() : "-";
	    String mensajeError  = (event.motivo() != null && !event.motivo().isBlank())
	            ? event.motivo()
	            : "Sin detalle";

	    String contenidoHtml =
	        "<html>" +
	          "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
	            "<div style='max-width:700px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +

	              "<h2 style='color:#333; margin-top:0;'>" + estadoEmoji + " Stripe checkout (" + estadoTexto + ")</h2>" +

	              "<p><b>Flow:</b> " + flujo + "</p>" +

	              "<h3 style='margin-top:24px;'>Client</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                "<li><b>Name:</b> " + nombreCliente + "</li>" +
	                "<li><b>Email:</b> " + emailCliente + "</li>" +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Cart items</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                (detallesCarrito.length() > 0 ? detallesCarrito.toString() : "<li>No items</li>") +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Error message</h3>" +
	              "<p>" + mensajeError + "</p>" +

	              "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
	              "<p style='font-size:12px; color:#777;'>This email was generated automatically from the Stripe checkout flow.</p>" +

	            "</div>" +
	          "</body>" +
	        "</html>";

	    sendHtml(adminEmail, asunto, contenidoHtml);
	}



  
  // ======================
  // Notificaciones ADMIN – NO STRIPE
  // ======================
  
  public void enviarNotificacionAdminCompraNoStripe(CompraNoStripeAdminEvent event) {
	    String adminEmail = adminTo;

	    String estadoEmoji = event.exito() ? "✅" : "❌";
	    String estadoTexto = event.exito() ? "SUCCESS" : "ERROR";

	    String asunto = estadoEmoji + " NoStripe checkout – " + estadoTexto;

	    StringBuilder detallesCarrito = new StringBuilder();
	    if (event.carrito() != null && event.carrito().getItems() != null) {
	      event.carrito().getItems().forEach(item -> {
	        detallesCarrito
	            .append("<li>")
	            .append("<b>Type:</b> ").append(item.getTipo()).append(" | ")
	            .append("<b>Name:</b> ").append(item.getNombre()).append(" | ")
	            .append("<b>Qty:</b> ").append(item.getCantidad()).append(" | ")
	            .append("<b>Price:</b> ").append(item.getPrecio()).append(" EUR")
	            .append("</li>");
	      });
	    }

	    String nombreCliente = event.cliente() != null ? event.cliente().getNombre() : "-";
	    String emailCliente  = event.cliente() != null ? event.cliente().getEmail()  : "-";

	    String codigoCompra  = (event.compra() != null) ? event.compra().getCodigoCompra() : "-";
	    String totalCompra   = (event.compra() != null && event.compra().getTotal() != null)
	        ? event.compra().getTotal().toString()
	        : "-";
	    String pagado        = (event.compra() != null) ? String.valueOf(event.compra().getPagado()) : "-";
	    String fechaCompra   = (event.compra() != null && event.compra().getFechaCompra() != null)
	        ? event.compra().getFechaCompra().toString()
	        : "-";

	    String mensajeError  = (!event.exito() && event.mensajeError() != null)
	        ? event.mensajeError()
	        : "-";

	    String contenidoHtml =
	        "<html>" +
	          "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
	            "<div style='max-width:700px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +

	              "<h2 style='color:#333; margin-top:0;'>" + estadoEmoji + " NoStripe checkout (" + estadoTexto + ")</h2>" +

	              "<p><b>Flow:</b> " + event.flujo() + "</p>" +

	              "<h3 style='margin-top:24px;'>Client</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                "<li><b>Name:</b> " + nombreCliente + "</li>" +
	                "<li><b>Email:</b> " + emailCliente + "</li>" +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Order</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                "<li><b>Code:</b> " + codigoCompra + "</li>" +
	                "<li><b>Total:</b> " + totalCompra + " EUR</li>" +
	                "<li><b>Paid flag:</b> " + pagado + "</li>" +
	                "<li><b>Date:</b> " + fechaCompra + "</li>" +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Cart items</h3>" +
	              "<ul style='line-height:1.7; padding-left:20px;'>" +
	                 (detallesCarrito.length() > 0 ? detallesCarrito.toString() : "<li>No items</li>") +
	              "</ul>" +

	              "<h3 style='margin-top:24px;'>Error message</h3>" +
	              "<p>" + mensajeError + "</p>" +

	              "<hr style='border:none; border-top:1px solid #eee; margin:20px 0;'/>" +
	              "<p style='font-size:12px; color:#777;'>This email was generated automatically from the NoStripe checkout flow (gift card / free purchase).</p>" +

	            "</div>" +
	          "</body>" +
	        "</html>";

	    sendHtml(adminEmail, asunto, contenidoHtml);
	  }


  /* ===================== PDF de tarjeta regalo  ===================== */

  /* ===== Wrapper con firma original (compat) ===== */
  private byte[] generarTarjetaRegaloPdf(
      String codigo,
      String nombreReceptor,
      BigDecimal cantidad,
      String nombreCliente,
      LocalDate fechaExpiracion
  ) throws Exception {
    return generarTarjetaRegaloPdf(
        codigo, nombreReceptor, cantidad, nombreCliente, fechaExpiracion,
        new Locale("es", "ES"), Currency.getInstance("EUR"), true
    );
  }

  /* ===== Tarjeta regalo formato CUADRADO 1×1 (148x148 mm) + imagen completa + layout como la plantilla ===== */
  private byte[] generarTarjetaRegaloPdf(
      String codigo,
      String nombreReceptor,
      BigDecimal cantidad,
      String nombreCliente,
      LocalDate fechaExpiracion,
      Locale locale,
      Currency currency,
      boolean autoFit
  ) throws Exception {

    if (locale == null) locale = Locale.getDefault();
    if (currency == null) currency = Currency.getInstance("EUR");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    float mm = 72f / 25.4f;

    // === CUADRADO 148 x 148 mm ===
    Rectangle SQUARE = new Rectangle(148f * mm, 148f * mm);
    Document document = new Document(SQUARE);
    PdfWriter writer = PdfWriter.getInstance(document, baos);

    document.open();

    float pageW = document.getPageSize().getWidth();
    float pageH = document.getPageSize().getHeight();
    float centerX = pageW / 2f;

    // === IMAGEN FULL CUADRADO ===
    ClassPathResource resource = new ClassPathResource("img/plantillaTarjetaRegalo.png");
    try (InputStream is = resource.getInputStream()) {
      Image img = Image.getInstance(is.readAllBytes());

      float imgW = img.getWidth();
      float imgH = img.getHeight();
      float ratioImg = imgW / imgH;
      float ratioCard = pageW / pageH;

      float finalW, finalH;
      if (ratioImg > ratioCard) {
        finalH = pageH;
        finalW = finalH * ratioImg;
      } else {
        finalW = pageW;
        finalH = finalW / ratioImg;
      }

      img.scaleAbsolute(finalW, finalH);
      img.setAbsolutePosition((pageW - finalW) / 2f, (pageH - finalH) / 2f);
      document.add(img);
    }

    PdfContentByte canvas = writer.getDirectContent();

    // ========== FUENTES QUICKSAND REGULAR / BOLD ==========
    BaseFont quicksandBold = loadFontOrFallback("fonts/Quicksand-Bold.ttf", BaseFont.HELVETICA_BOLD);
    BaseFont quicksandRegular = loadFontOrFallback("fonts/Quicksand-Regular.ttf", BaseFont.HELVETICA);

    // ========= FORMATEOS =========
    NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    nf.setCurrency(currency);
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(0);

    String importe = nf.format(cantidad != null ? cantidad : BigDecimal.ZERO);

    DateTimeFormatter df = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(locale);
    String fechaStr = (fechaExpiracion != null) ? fechaExpiracion.format(df) : "";

    String codeStr = (codigo != null) ? codigo : "";
    String fromStr = (nombreCliente != null) ? nombreCliente : "";
    String toStr   = (nombreReceptor != null) ? nombreReceptor : "";

    // ========= ESTILO / COLORES =========
    BaseColor pillWhite = new BaseColor(255, 255, 255);
    BaseColor textBlack = BaseColor.BLACK;
    BaseColor teal = new BaseColor(62, 140, 145); // parecido al ejemplo
    BaseColor textWhite = BaseColor.WHITE;

    // ========= 1) PILL SUPERIOR: "Voucher for <to>" =========
    String topLeft = "Voucher for ";
    String topName = toStr;

    float topPillW = pageW * 0.60f;
    float topPillH = pageH * 0.085f;
    float topPillX = (pageW - topPillW) / 2f;
    float topPillY = pageH * 0.76f;

    drawRoundedRect(canvas, topPillX, topPillY, topPillW, topPillH, topPillH / 2f, pillWhite, 1f);

    float topFontSize = 18f;
    if (autoFit) topFontSize = fitFontSizeForPill(
        canvas, quicksandRegular, quicksandBold,
        topLeft, topName,
        topPillW * 0.90f, topFontSize, 12f
    );

    drawTwoPartCenteredText(
        canvas,
        topLeft, quicksandRegular,
        topName, quicksandBold,
        centerX,
        topPillY + (topPillH * 0.30f),
        topFontSize,
        textBlack
    );

    // ========= 2) TITULO: "Ashya" + "Art & Ceramics Atelier" =========
    float title1Size = 46f;
    float title2Size = 40f;

    if (autoFit) {
      title2Size = Math.max(26f,
          fitFontSizeSingleLine(canvas, quicksandRegular, "Art & Ceramics Atelier", pageW * 0.92f, title2Size, 22f)
      );
      title1Size = Math.max(36f, Math.min(title1Size, title2Size + 18f));
    }

    float title1Y = pageH * 0.60f;
    float title2Y = pageH * 0.50f;

    drawTextCentered(canvas, "Ashya", centerX, title1Y, title1Size, quicksandRegular, textBlack);
    drawTextCentered(canvas, "Art & Ceramics Atelier", centerX, title2Y, title2Size, quicksandRegular, textBlack);

    // ========= 3) BADGE CÓDIGO (turquesa): "Code: XXXX" =========
    String codeLabel = "Code: " + codeStr;

    float codePillW = pageW * 0.50f;
    float codePillH = pageH * 0.075f;
    float codePillX = (pageW - codePillW) / 2f;
    float codePillY = pageH * 0.38f;

    drawRoundedRect(canvas, codePillX, codePillY, codePillW, codePillH, codePillH / 2f, teal, 1f);

    float codeFontSize = 20f;
    if (autoFit) codeFontSize = fitFontSizeSingleLine(
        canvas, quicksandRegular, codeLabel, codePillW * 0.90f, codeFontSize, 12f
    );

    drawTextCentered(canvas, codeLabel, centerX, codePillY + (codePillH * 0.28f), codeFontSize, quicksandRegular, textWhite);

    // ========= 4) IMPORTE =========
    float amountSize = 30f;
    if (autoFit) {
      amountSize = Math.max(20f,
          fitFontSizeSingleLine(canvas, quicksandBold, importe, pageW * 0.70f, amountSize, 16f)
      );
    }
    drawTextCentered(canvas, importe, centerX, pageH * 0.30f, amountSize, quicksandBold, textBlack);

    // ========= 5) PILL INFERIOR: "Valid until xx" =========
    String validLabel = "Valid until " + (fechaStr.isBlank() ? "—" : fechaStr);

    float botPillW = pageW * 0.60f;
    float botPillH = pageH * 0.085f;
    float botPillX = (pageW - botPillW) / 2f;
    float botPillY = pageH * 0.16f;

    drawRoundedRect(canvas, botPillX, botPillY, botPillW, botPillH, botPillH / 2f, pillWhite, 1f);

    float botFontSize = 18f;
    if (autoFit) {
      botFontSize = Math.max(12f,
          fitFontSizeSingleLine(canvas, quicksandRegular, validLabel, botPillW * 0.90f, botFontSize, 12f)
      );
    }

    drawTextCentered(canvas, validLabel, centerX, botPillY + (botPillH * 0.30f), botFontSize, quicksandRegular, textBlack);

    document.close();
    return baos.toByteArray();
  }

  /* ===================== HELPERS ===================== */

  private BaseFont loadFontOrFallback(String classpath, String fallbackBaseFont) {
    try {
      ClassPathResource rb = new ClassPathResource(classpath);
      try (InputStream isB = rb.getInputStream()) {
        return BaseFont.createFont(
            classpath.substring(classpath.lastIndexOf('/') + 1),
            BaseFont.IDENTITY_H,
            BaseFont.EMBEDDED,
            true,
            isB.readAllBytes(),
            null
        );
      }
    } catch (Exception e) {
      try {
        return BaseFont.createFont(fallbackBaseFont, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  private void drawTextCentered(PdfContentByte canvas, String text, float x, float y, float fontSize, BaseFont font, BaseColor color) {
    canvas.saveState();
    canvas.beginText();
    canvas.setFontAndSize(font, fontSize);
    canvas.setColorFill(color);
    canvas.showTextAligned(Element.ALIGN_CENTER, text != null ? text : "", x, y, 0);
    canvas.endText();
    canvas.restoreState();
  }

  private void drawRoundedRect(PdfContentByte canvas, float x, float y, float w, float h, float r, BaseColor fill, float fillOpacity) {
    canvas.saveState();
    PdfGState gs = new PdfGState();
    gs.setFillOpacity(fillOpacity);
    canvas.setGState(gs);

    canvas.setColorFill(fill);
    canvas.roundRectangle(x, y, w, h, r);
    canvas.fill();
    canvas.restoreState();
  }

  /**
   * Centra dos trozos de texto en la misma línea: "Voucher for " (regular) + "Jane Doe" (bold).
   * Calcula el ancho total y pinta ambos para que queden centrados en xCenter.
   */
  private void drawTwoPartCenteredText(
      PdfContentByte canvas,
      String leftText, BaseFont leftFont,
      String rightText, BaseFont rightFont,
      float xCenter,
      float y,
      float fontSize,
      BaseColor color
  ) {
    String lt = leftText != null ? leftText : "";
    String rt = rightText != null ? rightText : "";

    float leftW = leftFont.getWidthPoint(lt, fontSize);
    float rightW = rightFont.getWidthPoint(rt, fontSize);
    float totalW = leftW + rightW;

    float startX = xCenter - (totalW / 2f);

    canvas.saveState();
    canvas.beginText();
    canvas.setColorFill(color);

    canvas.setFontAndSize(leftFont, fontSize);
    canvas.showTextAligned(Element.ALIGN_LEFT, lt, startX, y, 0);

    canvas.setFontAndSize(rightFont, fontSize);
    canvas.showTextAligned(Element.ALIGN_LEFT, rt, startX + leftW, y, 0);

    canvas.endText();
    canvas.restoreState();
  }

  /** Ajusta fontSize para que una línea quepa en un ancho máximo. */
  private float fitFontSizeSingleLine(PdfContentByte canvas, BaseFont font, String text, float maxWidth, float startSize, float minSize) {
    String t = text != null ? text : "";
    float size = startSize;
    while (size > minSize) {
      float w = font.getWidthPoint(t, size);
      if (w <= maxWidth) return size;
      size -= 0.75f;
    }
    return minSize;
  }

  /** Ajusta fontSize para dos-part text dentro de una pill. */
  private float fitFontSizeForPill(PdfContentByte canvas, BaseFont leftFont, BaseFont rightFont,
                                   String left, String right, float maxWidth, float startSize, float minSize) {
    String l = left != null ? left : "";
    String r = right != null ? right : "";
    float size = startSize;
    while (size > minSize) {
      float w = leftFont.getWidthPoint(l, size) + rightFont.getWidthPoint(r, size);
      if (w <= maxWidth) return size;
      size -= 0.75f;
    }
    return minSize;
  }











  /* Interfaz funcional auxiliar */
  @FunctionalInterface
  private interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
  }
}
