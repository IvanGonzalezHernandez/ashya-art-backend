package com.ashyaart.ashya_art_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.event.CompraEventos.ReservaCursoCanceladaEvent;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;

@ExtendWith(MockitoExtension.class)
class CursoCompraServiceCancelacionTest {

    @Mock private CursoCompraDao cursoCompraDao;
    @Mock private CursoFechaDao cursoFechaDao;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private CursoCompraService service;

    private CursoFecha fecha;
    private Cliente cliente;

    @BeforeEach
    void preparar() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNombre("Wheel throwing");
        fecha = new CursoFecha(curso, LocalDate.of(2026, 10, 17), 3, LocalTime.of(18, 0), LocalTime.of(20, 0));
        fecha.setId(10L);

        cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setEmail("ana@example.com");

        when(cursoCompraDao.findById(5L)).thenReturn(Optional.of(new CursoCompra(fecha, cliente, 2, null)));
    }

    @Test
    void liberaLasPlazasYAvisaAlCliente() {
        when(cursoCompraDao.borradoLogico(5L)).thenReturn(1);

        service.eliminarProducto(5L);

        verify(cursoFechaDao).sumarPlazas(10L, 2);
        ArgumentCaptor<ReservaCursoCanceladaEvent> evento = ArgumentCaptor.forClass(ReservaCursoCanceladaEvent.class);
        verify(eventPublisher).publishEvent(evento.capture());
        assertEquals("ana@example.com", evento.getValue().email());
        assertEquals("Wheel throwing", evento.getValue().nombreCurso());
        assertEquals(LocalDate.of(2026, 10, 17), evento.getValue().fecha());
        assertEquals("18:00", evento.getValue().horaInicio());
        assertEquals(2, evento.getValue().plazas());
    }

    @Test
    void unaReservaYaCanceladaNoVuelveAAvisar() {
        when(cursoCompraDao.borradoLogico(5L)).thenReturn(0);

        service.eliminarProducto(5L);

        verify(cursoFechaDao, never()).sumarPlazas(anyLong(), anyInt());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void sinEmailDelClienteCancelaIgualPeroNoAvisa() {
        cliente.setEmail(" ");
        when(cursoCompraDao.borradoLogico(5L)).thenReturn(1);

        service.eliminarProducto(5L);

        verify(cursoFechaDao).sumarPlazas(10L, 2);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
