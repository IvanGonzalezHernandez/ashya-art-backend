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
  @Value("${resend.api.key:}")
  private String apiKey;

  @Value("${resend.from:}")
  private String from;

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

  /* ===== Versión flexible: locale/moneda + auto-fit opcional ===== */
  private byte[] generarTarjetaRegaloPdf(
      String codigo,
      String nombreReceptor,
      BigDecimal cantidad,
      String nombreCliente,
      LocalDate fechaExpiracion,
      Locale locale,
      Currency currency,
      boolean autoFit // si true, aplica reducción proporcional si no cabe en el tercio inferior
  ) throws Exception {

    if (locale == null) locale = Locale.getDefault();
    if (currency == null) currency = Currency.getInstance("EUR");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4);
    PdfWriter writer = PdfWriter.getInstance(document, baos);
    document.open();

    // Fondo: plantilla
    ClassPathResource resource = new ClassPathResource("img/plantillaTarjetaRegalo.png");
    try (InputStream is = resource.getInputStream()) {
      Image img = Image.getInstance(is.readAllBytes());
      img.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
      img.setAbsolutePosition(0, 0);
      document.add(img);
    }

    PdfContentByte canvas = writer.getDirectContent();

    // Fuentes Quicksand embebidas
    BaseFont quicksandRegular;
    BaseFont quicksandBold;
    try {
      ClassPathResource qsr = new ClassPathResource("fonts/Quicksand-Regular.ttf");
      ClassPathResource qsb = new ClassPathResource("fonts/Quicksand-Bold.ttf");
      try (InputStream isR = qsr.getInputStream(); InputStream isB = qsb.getInputStream()) {
        quicksandRegular = BaseFont.createFont(
            "Quicksand-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, isR.readAllBytes(), null);
        quicksandBold = BaseFont.createFont(
            "Quicksand-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, isB.readAllBytes(), null);
      }
    } catch (Exception e) {
      quicksandRegular = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      quicksandBold    = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }

    // Helper: texto con fondo blanco opaco (sin transparencia)
    TriConsumer<String, float[], BaseFont> drawTextWithBackground = (text, pos, font) -> {
      try {
        float x = pos[0], y = pos[1], fontSize = pos[2];
        float padX = 8f, padY = 4f;

        canvas.saveState();
        canvas.setFontAndSize(font, fontSize);

        float textWidth  = font.getWidthPoint(text, fontSize);
        float textHeight = fontSize;

        // Rectángulo blanco opaco
        canvas.setColorFill(BaseColor.WHITE);
        canvas.rectangle(
            x - textWidth / 2f - padX,
            y - padY,
            textWidth + 2f * padX,
            textHeight + 2f * padY
        );
        canvas.fill();

        // Texto en negro
        canvas.beginText();
        canvas.setColorFill(BaseColor.BLACK);
        canvas.setFontAndSize(font, fontSize);
        canvas.showTextAligned(Element.ALIGN_CENTER, text, x, y, 0);
        canvas.endText();

        canvas.restoreState();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    };

    // Layout: tercio inferior
    float pageW = document.getPageSize().getWidth();
    float pageH = document.getPageSize().getHeight();

    float thirdH = pageH / 3f;
    float areaBottomMargin = 24f;
    float areaTopMargin = 24f;
    float areaHeight = thirdH - areaTopMargin - areaBottomMargin;
    float areaTopY   = thirdH - areaTopMargin;
    float centerX    = pageW / 2f;

    // Tamaños base
    float titleSize = 26f;
    float lineSize  = 18f;
    float gap       = 26f;

    // Formateos dependientes de locale
    NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    nf.setCurrency(currency);
    String importe = cantidad != null ? nf.format(cantidad) : nf.format(0);

    DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
    String fechaStr = (fechaExpiracion != null) ? fechaExpiracion.format(df) : "";

    // Contenido
    String[] lines = new String[] {
        "Gift Card",
        "Code: " + (codigo != null ? codigo : ""),
        "To: " + (nombreReceptor != null ? nombreReceptor : ""),
        "From: " + (nombreCliente != null ? nombreCliente : ""),
        "Amount: " + importe,
        "Valid until: " + fechaStr
    };

    // ===== Ajuste opcional para encajar (autoFit) =====
    if (autoFit) {
      // Altura necesaria: título ocupa (titleSize) y resto 5 líneas ocupan 5*lineSize + 5*gap + 1*gap para tras el título
      int totalLines = lines.length;
      float linesHeight = titleSize + gap              // Gift Card + gap siguiente
                        + (totalLines - 1) * lineSize  // resto de líneas
                        + (totalLines - 1) * gap;      // gaps entre resto de líneas

      if (linesHeight > areaHeight) {
        float scale = areaHeight / linesHeight; // factor <= 1
        // Escala homogénea de tamaños y gap, pero con mínimos razonables
        titleSize = Math.max(16f, titleSize * scale);
        lineSize  = Math.max(12f, lineSize  * scale);
        gap       = Math.max(12f, gap       * scale);
      }
    }

    // Pintado
    float y = areaTopY; // empezamos arriba del tercio inferior
    // Título
    drawTextWithBackground.accept(lines[0], new float[]{centerX, y, titleSize}, quicksandBold);
    y -= (titleSize + gap);

    // Resto
    for (int i = 1; i < lines.length; i++) {
      drawTextWithBackground.accept(lines[i], new float[]{centerX, y, lineSize}, quicksandRegular);
      y -= gap;
    }

    document.close();
    return baos.toByteArray();
  }

  /* Interfaz funcional auxiliar */
  @FunctionalInterface
  private interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
  }
}
