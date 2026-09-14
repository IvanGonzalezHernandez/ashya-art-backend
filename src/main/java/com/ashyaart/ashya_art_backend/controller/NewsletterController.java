package com.ashyaart.ashya_art_backend.controller;

import java.util.List;
import java.util.Map;

import com.ashyaart.ashya_art_backend.filter.NewsletterFilter;
import com.ashyaart.ashya_art_backend.model.NewsletterCampanaDto;
import com.ashyaart.ashya_art_backend.model.NewsletterDto;
import com.ashyaart.ashya_art_backend.service.NewsletterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletters")
public class NewsletterController {

    private static final Logger logger = LoggerFactory.getLogger(NewsletterController.class);

    @Autowired
    private NewsletterService newsletterService;

    @GetMapping
    public ResponseEntity<List<NewsletterDto>> findByFilter(NewsletterFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener newsletters");
        List<NewsletterDto> newslettersDto = newsletterService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} newsletters", newslettersDto.size());
        return ResponseEntity.ok(newslettersDto);
    }

    @PostMapping
    public ResponseEntity<NewsletterDto> crearNewsletter(@RequestBody NewsletterDto newsletterDto) {
        logger.info("crearNewsletter - Solicitud POST para crear un nuevo newsletter: {}", newsletterDto);
        NewsletterDto nuevoNewsletter = newsletterService.crearNewsletter(newsletterDto);
        logger.info("crearNewsletter - Newsletter creado con ID: {}", nuevoNewsletter.getId());
        return ResponseEntity.ok(nuevoNewsletter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsletterDto> actualizarNewsletter(@PathVariable Long id, @RequestBody NewsletterDto newsletterDto) {
        logger.info("actualizarNewsletter - Solicitud PUT para actualizar newsletter con ID {}: {}", id, newsletterDto);
        newsletterDto.setId(id);
        NewsletterDto newsletterActualizado = newsletterService.actualizarNewsletter(newsletterDto);
        logger.info("actualizarNewsletter - Newsletter actualizado con ID: {}", newsletterActualizado.getId());
        return ResponseEntity.ok(newsletterActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNewsletter(@PathVariable Long id) {
        logger.info("eliminarNewsletter - Solicitud DELETE para eliminar newsletter con ID: {}", id);
        newsletterService.eliminarNewsletter(id);
        logger.info("eliminarNewsletter - Newsletter con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/suscribirse")
    public ResponseEntity<NewsletterDto> suscribirse(@RequestBody NewsletterDto newsletterDto) {
        logger.info("suscribirse - Solicitud POST para suscripción con email: {}", newsletterDto.getEmail());
        NewsletterDto nuevoNewsletter = newsletterService.crearNewsletter(newsletterDto);
        logger.info("suscribirse - Newsletter creado con ID: {}", nuevoNewsletter.getId());
        return ResponseEntity.ok(nuevoNewsletter);
    }
    
    @PostMapping("/suscribirse-checkout")
    public ResponseEntity<Void> suscribirseCheckout(@RequestBody NewsletterDto newsletterDto) {
        logger.info("suscribirseCheckout - Intento de suscripción silenciosa para email: {}", newsletterDto != null ? newsletterDto.getEmail() : null);

        try {
            if (newsletterDto != null && newsletterDto.getEmail() != null) {
                newsletterService.suscribirNewsletterCheckout(newsletterDto.getEmail());
            }
        } catch (Exception e) {
            logger.warn("suscribirseCheckout - Error silencioso en suscripción newsletter", e);
        }

        // Siempre OK para no interferir con el pago
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/send-campaign")
    public ResponseEntity<Map<String, Object>> enviarCampana(@RequestBody NewsletterCampanaDto dto) {
        logger.info("enviarCampana - Solicitud POST para enviar campana de newsletter. Asunto: {}",
                dto != null ? dto.getAsunto() : null);
        Map<String, Object> resultado = newsletterService.enviarCampana(dto);
        logger.info("enviarCampana - Resultado: {}", resultado);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam("email") String email) {
        logger.info("unsubscribe - Solicitud GET para desuscribir email: {}", email);
        boolean desuscrito = newsletterService.desuscribirPorEmail(email);

        String html;
        if (desuscrito) {
            logger.info("unsubscribe - Email {} desuscrito correctamente", email);
            html = paginaDesuscripcion(
                    "You've been unsubscribed",
                    "You will no longer receive our newsletter. We're sad to see you go &mdash; you're always welcome back!"
            );
        } else {
            logger.warn("unsubscribe - No se encontró suscripción activa para email: {}", email);
            html = paginaDesuscripcion(
                    "Already unsubscribed",
                    "This email wasn't subscribed, or has already been unsubscribed from our newsletter."
            );
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    /** Pagina de confirmacion con la estetica de Ashya Art, mostrada tras pulsar el enlace de unsubscribe del email. */
    private String paginaDesuscripcion(String titulo, String mensaje) {
        return "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
                "<meta charset='UTF-8'/>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'/>" +
                "<title>Ashya Art</title>" +
                "<link rel='preconnect' href='https://fonts.googleapis.com'/>" +
                "<link href='https://fonts.googleapis.com/css2?family=Quicksand:wght@500;600;700&display=swap' rel='stylesheet'/>" +
                "<style>" +
                    "body{margin:0;min-height:100vh;display:flex;align-items:center;justify-content:center;" +
                        "background-color:#F9F3EC;font-family:'Quicksand',Arial,sans-serif;padding:24px;box-sizing:border-box;}" +
                    ".card{background:#fff;max-width:420px;width:100%;padding:40px 32px;border-radius:16px;" +
                        "text-align:center;box-shadow:0 12px 28px rgba(0,0,0,.08);}" +
                    ".card img{width:70px;height:70px;border-radius:50%;margin-bottom:20px;}" +
                    ".card h1{color:#3E3028;font-size:1.4rem;font-weight:700;margin:0 0 12px;}" +
                    ".card p{color:#5c5049;line-height:1.6;margin:0 0 28px;}" +
                    ".card a.btn{display:inline-block;background-color:#3A9097;color:#fff;text-decoration:none;" +
                        "padding:11px 28px;border-radius:8px;font-weight:600;}" +
                    ".card a.btn:hover{background-color:#317b81;}" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class='card'>" +
                    "<img src='https://ashya-art.com/assets/logo/logo.png' alt='Ashya Art'/>" +
                    "<h1>" + titulo + "</h1>" +
                    "<p>" + mensaje + "</p>" +
                    "<a class='btn' href='https://ashya-art.com'>Back to Ashya Art</a>" +
                "</div>" +
            "</body>" +
            "</html>";
    }

}
