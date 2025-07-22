package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.model.NewsletterDto;
import com.ashyaart.ashya_art_backend.entity.Newsletter;

public class NewsletterAssembler {

    public static NewsletterDto toDto(Newsletter newsletter) {
        NewsletterDto dto = new NewsletterDto();
        dto.setId(newsletter.getId());
        dto.setEmail(newsletter.getEmail());
        dto.setFechaRegistro(newsletter.getFechaRegistro());
        dto.setFechaBaja(newsletter.getFechaBaja());
        dto.setEstado(newsletter.getEstado());
        return dto;
    }

    public static Newsletter toEntity(NewsletterDto dto) {
        Newsletter newsletter = new Newsletter();
        newsletter.setId(dto.getId());
        newsletter.setEmail(dto.getEmail());
        newsletter.setFechaRegistro(dto.getFechaRegistro());
        newsletter.setFechaBaja(dto.getFechaBaja());
        newsletter.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        return newsletter;
    }
}
