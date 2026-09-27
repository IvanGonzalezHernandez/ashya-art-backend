package com.ashyaart.ashya_art_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.ashyaart.ashya_art_backend.repository.CarritoDao;

class CarritoLimpiezaServiceTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-09-27T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void borraLosCarritosDeMasDeTreintaDias() {
        CarritoDao dao = mock(CarritoDao.class);
        new CarritoLimpiezaService(dao, 30, reloj).limpiar();

        ArgumentCaptor<LocalDateTime> limite = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(dao).borrarCreadosAntesDe(limite.capture());
        assertEquals(LocalDateTime.of(2026, 8, 28, 12, 0), limite.getValue());
    }

    @Test
    void nuncaGuardaMenosDeUnaSemana() {
        CarritoDao dao = mock(CarritoDao.class);
        new CarritoLimpiezaService(dao, 1, reloj).limpiar();

        ArgumentCaptor<LocalDateTime> limite = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(dao).borrarCreadosAntesDe(limite.capture());
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 0), limite.getValue());
    }

    @Test
    void unErrorDeBaseDeDatosNoSePropaga() {
        CarritoDao dao = mock(CarritoDao.class);
        when(dao.borrarCreadosAntesDe(any())).thenThrow(new RuntimeException("sin conexión"));
        new CarritoLimpiezaService(dao, 30, reloj).limpiar();
    }
}
