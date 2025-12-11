package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import com.ashyaart.ashya_art_backend.filter.NewsletterFilter;
import com.ashyaart.ashya_art_backend.model.NewsletterDto;
import com.ashyaart.ashya_art_backend.service.NewsletterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @GetMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam("email") String email) {
        logger.info("unsubscribe - Solicitud GET para desuscribir email: {}", email);
        boolean desuscrito = newsletterService.desuscribirPorEmail(email);

        if (desuscrito) {
            logger.info("unsubscribe - Email {} desuscrito correctamente", email);
            return ResponseEntity.ok("You have been unsubscribed from the newsletter.");
        } else {
            logger.warn("unsubscribe - No se encontró suscripción activa para email: {}", email);
            return ResponseEntity.ok("This email was not subscribed or is already unsubscribed.");
        }
    }

    
    
}
