package com.ashyaart.ashya_art_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ashyaart.ashya_art_backend.assembler.NewsletterAssembler;
import com.ashyaart.ashya_art_backend.entity.Newsletter;
import com.ashyaart.ashya_art_backend.filter.NewsletterFilter;
import com.ashyaart.ashya_art_backend.model.NewsletterDto;
import com.ashyaart.ashya_art_backend.repository.NewsletterDao;

@Service
public class NewsletterService {

    private static final Logger logger = LoggerFactory.getLogger(NewsletterService.class);

    @Autowired
    private NewsletterDao newsletterDao;

    @Autowired
    private EmailService emailService;

    /* ===== Helper para normalizar email ===== */
    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /* ================= BÚSQUEDA ================= */

    public List<NewsletterDto> findByFilter(NewsletterFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de newsletters");
        List<Newsletter> newsletters = newsletterDao.findByFiltros(filter.getEmail());
        List<NewsletterDto> resultado = newsletters.stream()
                .map(NewsletterAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} newsletters", resultado.size());
        return resultado;
    }

    /* ================= SUSCRIPCIÓN ================= */

    @Transactional
    public NewsletterDto crearNewsletter(NewsletterDto newsletterDto) {
        String emailNormalizado = normalizarEmail(newsletterDto.getEmail());
        logger.info("crearNewsletter - Suscripción para email normalizado: {}", emailNormalizado);

        if (emailNormalizado == null || emailNormalizado.isBlank()) {
            throw new IllegalArgumentException("El email de newsletter es obligatorio");
        }

        // Buscar si ya existe un registro con ese email
        Newsletter existente = newsletterDao.findByEmail(emailNormalizado);

        if (existente != null) {

            // Ya está suscrito -> ERROR
            if (Boolean.TRUE.equals(existente.getEstado())) {
                logger.warn("crearNewsletter - Email {} ya estaba suscrito. Lanzando 409.", emailNormalizado);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already subscribed.");
            }

            // Estaba de baja -> reactivar
            logger.info("crearNewsletter - Reactivando suscripción para email {}", emailNormalizado);
            existente.setEstado(true);
            existente.setFechaBaja(null);
            Newsletter reactivado = newsletterDao.save(existente);

            emailService.enviarConfirmacionNewsletter(emailNormalizado);

            return NewsletterAssembler.toDto(reactivado);
        }


        // No existe -> creamos nuevo
        Newsletter newsletter = new Newsletter();
        newsletter.setEmail(emailNormalizado);
        newsletter.setFechaRegistro(
            newsletterDto.getFechaRegistro() != null ? newsletterDto.getFechaRegistro() : LocalDate.now()
        );
        newsletter.setEstado(true);
        newsletter.setFechaBaja(null);

        Newsletter newsletterGuardado = newsletterDao.save(newsletter);

        // Enviar email HTML desde EmailService
        emailService.enviarConfirmacionNewsletter(emailNormalizado);

        NewsletterDto dtoGuardado = NewsletterAssembler.toDto(newsletterGuardado);
        logger.info("crearNewsletter - Newsletter creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    /* ================= DESUSCRIPCIÓN ================= */

    @Transactional
    public boolean desuscribirPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);

        if (emailNormalizado == null || emailNormalizado.isBlank()) {
            logger.warn("desuscribirPorEmail - Email vacío o nulo");
            return false;
        }

        // 👇 OJO: aquí es estado, no activo
        Newsletter entidad = newsletterDao.findByEmailAndEstadoTrue(emailNormalizado);

        if (entidad == null) {
            logger.warn("desuscribirPorEmail - No se encontró suscripción activa para email {}", emailNormalizado);
            return false;
        }

        entidad.setEstado(false);
        entidad.setFechaBaja(LocalDate.now());
        newsletterDao.save(entidad);

        logger.info("desuscribirPorEmail - Email {} desuscrito correctamente", emailNormalizado);
        return true;
    }

    /* ================= ACTUALIZAR ================= */

    @Transactional
    public NewsletterDto actualizarNewsletter(NewsletterDto newsletterDto) {
        logger.info("actualizarNewsletter - Actualizando newsletter con ID: {}", newsletterDto.getId());
        Newsletter newsletter = newsletterDao.findById(newsletterDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Newsletter no encontrado con ID: " + newsletterDto.getId()));

        // Aquí decide si quieres permitir cambiar el email o no
        newsletter.setEmail(normalizarEmail(newsletterDto.getEmail()));
        newsletter.setFechaRegistro(newsletterDto.getFechaRegistro());
        newsletter.setFechaBaja(newsletterDto.getFechaBaja());
        newsletter.setEstado(newsletterDto.getEstado());

        Newsletter newsletterActualizado = newsletterDao.save(newsletter);
        NewsletterDto dtoActualizado = NewsletterAssembler.toDto(newsletterActualizado);
        logger.info("actualizarNewsletter - Newsletter actualizado con ID: {}", dtoActualizado.getId());
        return dtoActualizado;
    }

    /* ================= BORRADO LÓGICO ================= */

    @Transactional
    public void eliminarNewsletter(Long id) {
        logger.info("eliminarNewsletter - Intentando eliminar newsletter con ID: {}", id);
        if (!newsletterDao.existsById(id)) {
            logger.warn("eliminarNewsletter - Newsletter con ID {} no encontrado", id);
            throw new RuntimeException("Newsletter con id " + id + " no encontrado");
        }
        Integer filasAfectadas = newsletterDao.borradoLogico(id);
        if (filasAfectadas == 0) {
            logger.error("eliminarNewsletter - No se pudo eliminar el newsletter con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar el newsletter con id " + id);
        }
        logger.info("eliminarNewsletter - Newsletter con ID {} eliminado correctamente (borrado lógico)", id);
    }
}