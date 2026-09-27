package com.ashyaart.ashya_art_backend.model;

/**
 * Página vista en la web pública, enviada por el frontend.
 * - entrada: true si es la primera página de la visita (la que trae referrer y utm_source).
 * - referrer: web desde la que llegó (solo en la entrada).
 * - utmSource: parámetro utm_source del enlace, si lo había.
 * - idioma: idioma elegido en la web (en, de, es).
 */
public record VisitaDto(String ruta, boolean entrada, String referrer, String utmSource, String idioma) {
}
