package com.ashyaart.ashya_art_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.ashyaart.ashya_art_backend.model.NewsletterCampanaDto;
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

            enviarConfirmacionSinRomperSuscripcion(emailNormalizado);

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
        enviarConfirmacionSinRomperSuscripcion(emailNormalizado);

        NewsletterDto dtoGuardado = NewsletterAssembler.toDto(newsletterGuardado);
        logger.info("crearNewsletter - Newsletter creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    /**
     * El email de confirmación es "best effort": si Resend falla (clave inválida, límite
     * alcanzado, red, etc.) la suscripción ya guardada en BD no debe perderse ni devolver
     * un 500 al cliente por un problema que no es suyo.
     */
    private void enviarConfirmacionSinRomperSuscripcion(String email) {
        try {
            emailService.enviarConfirmacionNewsletter(email);
        } catch (Exception e) {
            logger.warn("crearNewsletter - No se pudo enviar el email de confirmación a {}", email, e);
        }
    }
    
    /** ================= SUSCRIPCIÓN DESDE CHECKOUT ================= */
    @Transactional
    public void suscribirNewsletterCheckout(String email) {

        String emailNormalizado = normalizarEmail(email);

        if (emailNormalizado == null || emailNormalizado.isBlank()) {
            return; // en checkout, si viene vacío no cortamos nada
        }

        try {
            Newsletter existente = newsletterDao.findByEmail(emailNormalizado);

            // 1) Si ya existe y está activo -> no hacemos nada
            if (existente != null && Boolean.TRUE.equals(existente.getEstado())) {
                return;
            }

            // 2) Existe pero estaba de baja -> reactivar
            if (existente != null) {
                existente.setEstado(true);
                existente.setFechaBaja(null);

                if (existente.getFechaRegistro() == null) {
                    existente.setFechaRegistro(LocalDate.now());
                }

                newsletterDao.save(existente);
                return;
            }

            // 3) No existe -> crear
            Newsletter newsletter = new Newsletter();
            newsletter.setEmail(emailNormalizado);
            newsletter.setFechaRegistro(LocalDate.now());
            newsletter.setEstado(true);
            newsletter.setFechaBaja(null);

            newsletterDao.save(newsletter);

        } catch (Exception e) {
            logger.warn("suscribirNewsletterCheckout - No se pudo suscribir email=" + emailNormalizado + " (no se corta el pago)", e);
        }
    }

    /* ================= ENVIO DE CAMPAÑA ================= */

    /**
     * Envia un email a los suscriptores activos del newsletter (o a un unico email de
     * prueba, si se indica testEmail). No aborta el envio si falla un destinatario
     * concreto: sigue con el resto y devuelve cuantos se enviaron/fallaron.
     */
    public Map<String, Object> enviarCampana(NewsletterCampanaDto dto) {
        if (dto == null || dto.getAsunto() == null || dto.getAsunto().isBlank()
                || dto.getMensaje() == null || dto.getMensaje().isBlank()) {
            throw new IllegalArgumentException("El asunto y el mensaje son obligatorios");
        }

        String asunto = dto.getAsunto().trim();
        String mensajeHtml = formatearMensaje(dto.getMensaje());

        boolean esPrueba = dto.getTestEmail() != null && !dto.getTestEmail().isBlank();
        List<String> destinatarios;

        if (esPrueba) {
            destinatarios = List.of(normalizarEmail(dto.getTestEmail()));
            asunto = "[TEST] " + asunto;
        } else {
            // Colchon minimo de cuota de Resend: si queda menos de esto (diario o mensual),
            // no se manda la campana para no dejar sin margen los emails transaccionales
            // (confirmaciones de compra, tarjetas regalo, etc.).
            final int COLCHON_MINIMO = 30;
            Map<String, Object> uso = emailService.obtenerUsoResend();
            int dailyRemaining = ((Number) uso.get("dailyRemaining")).intValue();
            int monthlyRemaining = ((Number) uso.get("monthlyRemaining")).intValue();

            if (dailyRemaining < COLCHON_MINIMO || monthlyRemaining < COLCHON_MINIMO) {
                logger.warn("enviarCampana - Cuota insuficiente (daily={}, monthly={}), no se envia la campana",
                        dailyRemaining, monthlyRemaining);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Not enough Resend quota left to send a campaign (daily remaining: " + dailyRemaining +
                        ", monthly remaining: " + monthlyRemaining + "). A minimum of " + COLCHON_MINIMO +
                        " is required on both to leave room for other emails.");
            }

            List<String> emailsActivos = newsletterDao.findByEstadoTrue().stream()
                    .map(Newsletter::getEmail)
                    .filter(email -> email != null && !email.isBlank())
                    .toList();

            if (dto.getDestinatarios() != null && !dto.getDestinatarios().isEmpty()) {
                // Solo se admiten emails que sigan activos (por si la seleccion del admin
                // quedo desactualizada, p.ej. alguien se desuscribio mientras tanto).
                Set<String> seleccionados = dto.getDestinatarios().stream()
                        .map(this::normalizarEmail)
                        .collect(Collectors.toSet());
                destinatarios = emailsActivos.stream()
                        .filter(seleccionados::contains)
                        .toList();
            } else {
                destinatarios = emailsActivos;
            }

            if (destinatarios.isEmpty()) {
                throw new IllegalArgumentException("No active subscribers to send to.");
            }
        }

        logger.info("enviarCampana - Enviando '{}' a {} destinatario(s) (prueba={})",
                asunto, destinatarios.size(), esPrueba);

        int enviados = 0;
        int fallidos = 0;
        for (String destinatario : destinatarios) {
            try {
                emailService.enviarCampanaNewsletter(destinatario, asunto, mensajeHtml);
                enviados++;
            } catch (Exception e) {
                fallidos++;
                logger.error("enviarCampana - Error enviando a {}", destinatario, e);
            }
        }

        logger.info("enviarCampana - Enviados {}/{} (fallidos: {})", enviados, destinatarios.size(), fallidos);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("recipients", destinatarios.size());
        resultado.put("sent", enviados);
        resultado.put("failed", fallidos);
        resultado.put("test", esPrueba);
        return resultado;
    }

    /** Escapa HTML basico del texto escrito por el admin y convierte saltos de linea en &lt;br&gt;. */
    private String formatearMensaje(String texto) {
        String escapado = texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        return escapado.replace("\r\n", "\n").replace("\n", "<br>");
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