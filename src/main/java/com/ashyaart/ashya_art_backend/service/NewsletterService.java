package com.ashyaart.ashya_art_backend.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<NewsletterDto> findByFilter(NewsletterFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de newsletters");
        List<Newsletter> newsletters = newsletterDao.findByFiltros(filter.getEmail());
        List<NewsletterDto> resultado = newsletters.stream()
                .map(NewsletterAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} newsletters", resultado.size());
        return resultado;
    }

    @Transactional
    public NewsletterDto crearNewsletter(NewsletterDto newsletterDto) {
        logger.info("crearNewsletter - Creando nuevo newsletter: {}", newsletterDto);
        Newsletter newsletter = NewsletterAssembler.toEntity(newsletterDto);
        newsletter.setId(null);
        newsletter.setFechaRegistro(newsletterDto.getFechaRegistro() != null ? newsletterDto.getFechaRegistro() : LocalDate.now());
        Newsletter newsletterGuardado = newsletterDao.save(newsletter);
        NewsletterDto dtoGuardado = NewsletterAssembler.toDto(newsletterGuardado);
        logger.info("crearNewsletter - Newsletter creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public NewsletterDto actualizarNewsletter(NewsletterDto newsletterDto) {
        logger.info("actualizarNewsletter - Actualizando newsletter con ID: {}", newsletterDto.getId());
        Newsletter newsletter = newsletterDao.findById(newsletterDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Newsletter no encontrado con ID: " + newsletterDto.getId()));

        newsletter.setEmail(newsletterDto.getEmail());
        newsletter.setFechaRegistro(newsletterDto.getFechaRegistro());
        newsletter.setFechaBaja(newsletterDto.getFechaBaja());
        newsletter.setEstado(newsletterDto.getEstado());

        Newsletter newsletterActualizado = newsletterDao.save(newsletter);
        NewsletterDto dtoActualizado = NewsletterAssembler.toDto(newsletterActualizado);
        logger.info("actualizarNewsletter - Newsletter actualizado con ID: {}", dtoActualizado.getId());
        return dtoActualizado;
    }

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
