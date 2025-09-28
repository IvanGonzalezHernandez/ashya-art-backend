package com.ashyaart.ashya_art_backend.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  private final RestTemplate http = new RestTemplate();
  private final String apiKey;
  private final String from; // Ej: "Ashya Art <notifications@tu-dominio.com>"

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

  /* ===================== Métodos públicos (mismos que tenías) ===================== */

  public void enviarEmailConfirmacion(String para, String asunto, String cuerpo) {
    // mismo contenido que antes (texto plano)
    sendText(para, asunto, cuerpo);
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

    sendText(admin, asunto, cuerpo);
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

    sendText(emailCliente, asunto, cuerpo);
  }

  public void enviarConfirmacionCursoIndividual(String emailCliente, String nombreCliente,
                                                String nombreCurso, String fechaCurso,
                                                int plazasReservadas, BigDecimal bigDecimal,
                                                String informacionExtra) {
    String asunto = "Confirmation for your course - " + nombreCurso;

    String direccionTexto = "Ashya Art & Keramik Studio, Pinneberger Ch 74, 22523 Hamburg";
    String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" +
        URLEncoder.encode(direccionTexto, StandardCharsets.UTF_8);

    String cuerpoHtml =
        "<html>" +
            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
            "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
            "<h2 style='color:#333;'>Dear " + nombreCliente + ",</h2>" +
            "<p>Thank you for purchasing the <b>" + nombreCurso + "</b> from Ashya Art.</p>" +
            "<p>The course will start on <b>" + fechaCurso + "</b>.<br>" +
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
            "<li>📧 Email: <a href='mailto:ivangonzalez.code@gmail.com' style='color:#1a73e8; text-decoration:none;'>ivangonzalez.code@gmail.com</a></li>" +
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
    String asunto = "Confirmation for your product purchase - " + nombreProducto;

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
            "<li>📧 Email: <a href='mailto:ivangonzalez.code@gmail.com' style='color:#1a73e8; text-decoration:none;'>ivangonzalez.code@gmail.com</a></li>" +
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
    String asunto = "Your Secret Purchase - " + nombreSecreto;

    String cuerpoHtml =
        "<html>" +
            "<body style='background-color:#F9F3EC; font-family: Arial, sans-serif; color:#333; padding:20px;'>" +
            "<div style='max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;'>" +
            "<h2 style='color:#333;'>Dear " + nombreCliente + ",</h2>" +
            "<p>Thank you for your purchase from <b>Ashya Art</b>. Please find attached your PDF document containing the information/instruction you have purchased.</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li><b>🔐 Secret:</b> " + nombreSecreto + "</li>" +
            "</ul>" +
            "<p>If the attachment doesn’t open on your device, just reply to this email and I’ll resend it.</p>" +
            "<p>If you have any questions, you can contact me through any of the following options:</p>" +
            "<ul style='line-height:1.7; padding-left:20px;'>" +
            "<li>📞 Phone: <a href='tel:+491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📱 WhatsApp: <a href='https://wa.me/491638681397' style='color:#1a73e8; text-decoration:none;'>+49 163 8681397</a></li>" +
            "<li>📧 Email: <a href='mailto:ivangonzalez.code@gmail.com' style='color:#1a73e8; text-decoration:none;'>ivangonzalez.code@gmail.com</a></li>" +
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
      String contenido = "<h2>Hello " + nombreReceptor + "!</h2>" +
          "<p>You have received a gift card from <b>" + nombreCliente + "</b>.</p>" +
          "<p><b>Gift Card Code:</b> " + codigo + "</p>" +
          "<p><b>Amount:</b> €" + cantidad + "</p>" +
          "<p>This gift card is valid until <b>" + fechaExpiracion + "</b> (6 months).</p>" +
          "<p>You can redeem it in our online shop or courses at <a href='https://ashya-art.com'>Ashya Art</a>.</p>" +
          "<br><p>Enjoy your gift! 🎨</p>";

      sendHtmlWithAttachment(destinatario, asunto, contenido, "TarjetaRegalo_" + codigo + ".pdf", pdfBytes);

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
    // Cliente (texto)
    String asuntoCliente = "Firing Service Request Confirmation";
    String cuerpoCliente = "Hello " + nombreCliente + ",\n\n" +
        "We have received your request for the firing service: " + mapTipoServicio(tipoServicio) + ".\n" +
        "We will contact you soon to coordinate the details.\n\n" +
        "Best regards,\nAshya Art";
    sendText(emailCliente, asuntoCliente, cuerpoCliente);

    // Admin (texto)
    String admin = "ivangonzalez.code@gmail.com";
    String asuntoAdmin = "New Firing Service Request - " + mapTipoServicio(tipoServicio);

    String cuerpoAdmin = "You have received a new firing service request:\n\n" +
        "Name: " + nombreCliente + "\n" +
        "Email: " + emailCliente + "\n" +
        "Phone: " + telefonoCliente + "\n" +
        "Service type: " + mapTipoServicio(tipoServicio) + "\n" +
        "Number of pieces: " + numeroPiezas + "\n" +
        "Clay and glaze details: " + detallesMaterial + "\n" +
        "Additional questions: " + (preguntasAdicionales != null ? preguntasAdicionales : "None") + "\n\n" +
        "Please contact the client to coordinate the details.";
    sendText(admin, asuntoAdmin, cuerpoAdmin);
  }

  private String mapTipoServicio(String tipoServicio) {
    return switch (tipoServicio) {
      case "entireKilnBisque" -> "Rent entire kiln – Bisque firing (35€)";
      case "entireKilnGlaze" -> "Rent entire kiln – Glaze firing (40€)";
      case "singlePiece" -> "Single piece firing (4€)";
      default -> tipoServicio;
    };
  }

  /* ===================== PDF de tarjeta regalo (igual que tenías) ===================== */

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

    // Imagen base (resources/img/plantillaTarjetaRegalo.png)
    ClassPathResource resource = new ClassPathResource("img/plantillaTarjetaRegalo.png");
    try (InputStream is = resource.getInputStream()) {
      byte[] imgBytes = is.readAllBytes();
      Image img = Image.getInstance(imgBytes);
      img.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
      img.setAbsolutePosition(0, 0);
      document.add(img);
    }

    // Texto encima
    PdfContentByte canvas = writer.getDirectContent();
    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

    BiConsumer<String, float[]> drawTextWithBackground = (text, pos) -> {
      try {
        float x = pos[0];
        float y = pos[1];

        float padding = 5f;
        float fontSize = pos[2];
        canvas.setFontAndSize(bf, fontSize);

        float textWidth = bf.getWidthPoint(text, fontSize);
        float textHeight = fontSize;

        // Rectángulo blanco detrás del texto
        canvas.setColorFill(new BaseColor(255, 255, 255, 200)); // blanco semitransparente
        canvas.rectangle(x - textWidth / 2 - padding, y - padding, textWidth + 2 * padding, textHeight + 2 * padding);
        canvas.fill();

        // Escribir texto en negro
        canvas.beginText();
        canvas.setColorFill(BaseColor.BLACK);
        canvas.showTextAligned(Element.ALIGN_CENTER, text, x, y, 0);
        canvas.endText();

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };

    float pageWidth = document.getPageSize().getWidth();

    drawTextWithBackground.accept("Gift Card", new float[]{pageWidth / 2, 700, 24});
    drawTextWithBackground.accept("Code: " + codigo, new float[]{pageWidth / 2, 650, 20});
    drawTextWithBackground.accept("To: " + nombreReceptor, new float[]{pageWidth / 2, 600, 20});
    drawTextWithBackground.accept("From: " + nombreCliente, new float[]{pageWidth / 2, 550, 20});
    drawTextWithBackground.accept("Amount: €" + cantidad, new float[]{pageWidth / 2, 500, 20});
    drawTextWithBackground.accept("Valid until: " + fechaExpiracion, new float[]{pageWidth / 2, 450, 20});

    document.close();
    return baos.toByteArray();
  }
}
