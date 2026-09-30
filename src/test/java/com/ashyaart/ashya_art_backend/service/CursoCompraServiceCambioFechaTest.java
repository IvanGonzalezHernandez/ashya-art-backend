package com.ashyaart.ashya_art_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.event.CompraEventos.ReservaCursoReprogramadaEvent;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;

@ExtendWith(MockitoExtension.class)
class CursoCompraServiceCambioFechaTest {

    @Mock private CursoCompraDao cursoCompraDao;
    @Mock private CursoFechaDao cursoFechaDao;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private CursoCompraService service;

    private CursoFecha anterior;
    private CursoFecha nueva;
    private CursoCompra reserva;

    @BeforeEach
    void preparar() {
        Curso curso = curso(1L);
        anterior = fecha(10L, curso, LocalDate.now().plusDays(3));
        nueva = fecha(20L, curso, LocalDate.now().plusDays(10));

        Cliente cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setEmail("ana@example.com");

        reserva = new CursoCompra(anterior, cliente, 2, null);
        when(cursoCompraDao.findById(5L)).thenReturn(Optional.of(reserva));
    }

    @Test
    void mueveLasPlazasYAvisaAlCliente() {
        when(cursoFechaDao.findById(20L)).thenReturn(Optional.of(nueva));
        when(cursoFechaDao.descontarPlazas(20L, 2)).thenReturn(1);
        when(cursoCompraDao.save(reserva)).thenReturn(reserva);

        service.cambiarFecha(5L, 20L);

        verify(cursoFechaDao).descontarPlazas(20L, 2);
        verify(cursoFechaDao).sumarPlazas(10L, 2);
        assertSame(nueva, reserva.getCursoFecha());

        ArgumentCaptor<ReservaCursoReprogramadaEvent> evento = ArgumentCaptor.forClass(ReservaCursoReprogramadaEvent.class);
        verify(eventPublisher).publishEvent(evento.capture());
        assertEquals("ana@example.com", evento.getValue().email());
        assertEquals(anterior.getFecha(), evento.getValue().fechaAnterior());
        assertEquals(nueva.getFecha(), evento.getValue().fechaNueva());
        assertEquals(2, evento.getValue().plazas());
    }

    @Test
    void sinPlazasEnLaFechaNuevaNoLiberaNiMueveNada() {
        when(cursoFechaDao.findById(20L)).thenReturn(Optional.of(nueva));
        when(cursoFechaDao.descontarPlazas(20L, 2)).thenReturn(0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.cambiarFecha(5L, 20L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(cursoFechaDao, never()).sumarPlazas(anyLong(), anyInt());
        verify(cursoCompraDao, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
        assertSame(anterior, reserva.getCursoFecha());
    }

    @Test
    void rechazaUnaFechaDeOtroCurso() {
        CursoFecha deOtroCurso = fecha(30L, curso(2L), LocalDate.now().plusDays(10));
        when(cursoFechaDao.findById(30L)).thenReturn(Optional.of(deOtroCurso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.cambiarFecha(5L, 30L));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(cursoFechaDao, never()).descontarPlazas(anyLong(), anyInt());
    }

    @Test
    void rechazaUnaFechaPasada() {
        nueva.setFecha(LocalDate.now().minusDays(1));
        when(cursoFechaDao.findById(20L)).thenReturn(Optional.of(nueva));

        assertThrows(ResponseStatusException.class, () -> service.cambiarFecha(5L, 20L));
        verify(cursoFechaDao, never()).descontarPlazas(anyLong(), anyInt());
    }

    @Test
    void rechazaUnaFechaDadaDeBaja() {
        nueva.setEstado(false);
        when(cursoFechaDao.findById(20L)).thenReturn(Optional.of(nueva));

        assertThrows(ResponseStatusException.class, () -> service.cambiarFecha(5L, 20L));
        verify(cursoFechaDao, never()).descontarPlazas(anyLong(), anyInt());
    }

    @Test
    void rechazaUnaReservaCancelada() {
        reserva.setEstado(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.cambiarFecha(5L, 20L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(cursoFechaDao, never()).descontarPlazas(anyLong(), anyInt());
    }

    @Test
    void rechazaLaMismaFecha() {
        assertThrows(ResponseStatusException.class, () -> service.cambiarFecha(5L, 10L));
        verify(cursoFechaDao, never()).descontarPlazas(anyLong(), anyInt());
    }

    private static Curso curso(Long id) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNombre("Wheel throwing");
        return curso;
    }

    private static CursoFecha fecha(Long id, Curso curso, LocalDate dia) {
        CursoFecha fecha = new CursoFecha(curso, dia, 6, LocalTime.of(18, 0), LocalTime.of(20, 0));
        fecha.setId(id);
        return fecha;
    }
}
