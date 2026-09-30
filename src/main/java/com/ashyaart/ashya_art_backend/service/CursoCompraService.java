package com.ashyaart.ashya_art_backend.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ashyaart.ashya_art_backend.assembler.CursoCompraAssembler;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
import com.ashyaart.ashya_art_backend.event.CompraEventos.ReservaCursoReprogramadaEvent;
import com.ashyaart.ashya_art_backend.filter.CursoCompraFilter;
import com.ashyaart.ashya_art_backend.model.CursoCompraDto;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.ClienteDao;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;

@Service
public class CursoCompraService {

    private static final Logger logger = LoggerFactory.getLogger(CursoCompraService.class);

    @Autowired
    private CursoCompraDao cursoCompraDao;

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private CursoFechaDao cursoFechaDao;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<CursoCompraDto> findByFilter(CursoCompraFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de reservas");
        List<CursoCompra> reservas = cursoCompraDao.findByFiltros(filter.getIdCliente());
        List<CursoCompraDto> resultado = reservas.stream()
                .map(CursoCompraAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} reservas", resultado.size());
        return resultado;
    }

    @Transactional
    public CursoCompraDto crearProducto(CursoCompraDto dto) {
        logger.info("crearProducto - Creando nueva reserva: {}", dto);
        CursoCompra reserva = new CursoCompra();

        Cliente cliente = clienteDao.findById(dto.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
        CursoFecha fecha = cursoFechaDao.findById(dto.getIdFecha())
                .orElseThrow(() -> new EntityNotFoundException("CursoFecha no encontrado con ID: " + dto.getIdFecha()));

        reserva.setCliente(cliente);
        reserva.setCursoFecha(fecha);
        reserva.setPlazasReservadas(dto.getPlazasReservadas());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setPrecio(fecha.getCurso().getPrecio());

        CursoCompra guardado = cursoCompraDao.save(reserva);
        CursoCompraDto dtoGuardado = CursoCompraAssembler.toDto(guardado);
        logger.info("crearProducto - Reserva creada con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    /**
     * Cancela una reserva: borrado logico (se conserva para historial, deja de listarse) y
     * libera las plazas reservadas en CursoFecha para que vuelvan a estar disponibles.
     */
    @Transactional
    public void eliminarProducto(Long id) {
        logger.info("eliminarProducto - Cancelando reserva con ID: {}", id);
        CursoCompra reserva = cursoCompraDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva con id " + id + " no encontrada"));

        int filas = cursoCompraDao.borradoLogico(id);
        if (filas == 0) {
            logger.info("eliminarProducto - Reserva con ID {} ya estaba cancelada", id);
            return;
        }

        cursoFechaDao.sumarPlazas(reserva.getCursoFecha().getId(), reserva.getPlazasReservadas());
        logger.info("eliminarProducto - Reserva con ID {} cancelada correctamente (plazas liberadas)", id);
    }

    /**
     * Mueve una reserva a otra fecha del mismo curso (p. ej. el cliente no puede venir el dia
     * reservado). Todo en una transaccion: descuenta las plazas en la fecha nueva (de forma
     * atomica, falla si no hay sitio), libera las de la fecha anterior y avisa al cliente por email.
     */
    @Transactional
    public CursoCompraDto cambiarFecha(Long id, Long idFechaNueva) {
        logger.info("cambiarFecha - Moviendo reserva ID {} a la fecha ID {}", id, idFechaNueva);
        CursoCompra reserva = cursoCompraDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));
        if (!reserva.isEstado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This booking is cancelled and cannot be moved.");
        }

        CursoFecha anterior = reserva.getCursoFecha();
        if (anterior.getId().equals(idFechaNueva)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The booking is already on that date.");
        }

        CursoFecha nueva = cursoFechaDao.findById(idFechaNueva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course date not found."));
        if (!nueva.getCurso().getId().equals(anterior.getCurso().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The new date must belong to the same course.");
        }
        if (!Boolean.TRUE.equals(nueva.getEstado()) || nueva.getFecha().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The new date is not available.");
        }

        int plazas = reserva.getPlazasReservadas();
        if (cursoFechaDao.descontarPlazas(nueva.getId(), plazas) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough free seats on the new date.");
        }
        cursoFechaDao.sumarPlazas(anterior.getId(), plazas);

        reserva.setCursoFecha(nueva);
        CursoCompra guardada = cursoCompraDao.save(reserva);

        Cliente cliente = guardada.getCliente();
        if (cliente != null && cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            eventPublisher.publishEvent(
                new ReservaCursoReprogramadaEvent(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    nueva.getCurso().getNombre(),
                    anterior.getFecha(),
                    nueva.getFecha(),
                    nueva.getHoraInicio() != null ? nueva.getHoraInicio().toString() : "",
                    plazas
                )
            );
        } else {
            logger.warn("cambiarFecha - Reserva ID {} sin email de cliente, no se envia email", id);
        }

        logger.info("cambiarFecha - Reserva ID {} movida de la fecha ID {} a la ID {}", id, anterior.getId(), nueva.getId());
        return CursoCompraAssembler.toDto(guardada);
    }
}
