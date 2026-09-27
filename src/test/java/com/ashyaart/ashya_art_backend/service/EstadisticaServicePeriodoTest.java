package com.ashyaart.ashya_art_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EstadisticaServicePeriodoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 9, 27);

    @Test
    void treintaDiasYElPeriodoAnterior() {
        var p = EstadisticaService.Periodo.de("30d", HOY);
        assertEquals(LocalDate.of(2026, 8, 29), p.desde());
        assertEquals(LocalDate.of(2026, 7, 30), p.desdeAnterior());
        assertEquals(30, p.etiquetas().size());
        assertEquals("29 Aug", p.etiquetas().get(0));
        assertEquals("27 Sep", p.etiquetas().get(29));
        assertEquals(29, p.tramo(HOY, true));
        assertEquals(0, p.tramo(LocalDate.of(2026, 7, 30), false));
        assertEquals(29, p.tramo(LocalDate.of(2026, 8, 28), false));
    }

    @Test
    void sieteDias() {
        var p = EstadisticaService.Periodo.de("7d", HOY);
        assertEquals(LocalDate.of(2026, 9, 21), p.desde());
        assertEquals(LocalDate.of(2026, 9, 14), p.desdeAnterior());
        assertEquals(7, p.tramos());
    }

    @Test
    void doceMesesPorMes() {
        var p = EstadisticaService.Periodo.de("12m", HOY);
        assertEquals(LocalDate.of(2025, 10, 1), p.desde());
        assertEquals(LocalDate.of(2024, 10, 1), p.desdeAnterior());
        assertEquals("Oct 25", p.etiquetas().get(0));
        assertEquals("Sep 26", p.etiquetas().get(11));
        assertEquals(11, p.tramo(HOY, true));
        assertEquals(11, p.tramo(LocalDate.of(2025, 9, 30), false));
    }

    @Test
    void unPeriodoDesconocidoEsTreintaDias() {
        assertEquals("30d", EstadisticaService.Periodo.de("x", HOY).clave());
    }
}
