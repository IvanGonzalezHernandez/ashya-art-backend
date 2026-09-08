package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.CursoCompraAssembler;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.CursoFecha;
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
     * Actualizacion limitada desde el dashboard admin: plazas reservadas y/o fecha del curso
     * (reprogramar). El metodo de pago no se toca aqui: es fijo, se paga por la web o en el
     * atelier. Cualquier cambio de plazas u fecha ajusta cf.plazasDisponibles para que la
     * disponibilidad publica siga siendo correcta.
     */
    @Transactional
    public CursoCompraDto actualizarProducto(CursoCompraDto dto) {
        logger.info("actualizarProducto - Actualizando reserva con ID: {}", dto.getId());
        CursoCompra reserva = cursoCompraDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + dto.getId()));

        Integer plazasNuevas = dto.getPlazasReservadas();

        if (dto.getIdFecha() != null && !dto.getIdFecha().equals(reserva.getCursoFecha().getId())) {
            // Reprogramar: libera las plazas de la fecha anterior y descuenta de la nueva.
            CursoFecha fechaNueva = cursoFechaDao.findById(dto.getIdFecha())
                    .orElseThrow(() -> new EntityNotFoundException("CursoFecha no encontrada: " + dto.getIdFecha()));

            cursoFechaDao.sumarPlazas(reserva.getCursoFecha().getId(), reserva.getPlazasReservadas());

            int filasPlazas = cursoFechaDao.descontarPlazas(fechaNueva.getId(), plazasNuevas);
            if (filasPlazas == 0) {
                throw new IllegalStateException("No hay plazas suficientes en la fecha seleccionada");
            }

            reserva.setCursoFecha(fechaNueva);
            reserva.setPrecio(fechaNueva.getCurso().getPrecio());
        } else {
            int delta = plazasNuevas - reserva.getPlazasReservadas();
            if (delta > 0) {
                int filasPlazas = cursoFechaDao.descontarPlazas(reserva.getCursoFecha().getId(), delta);
                if (filasPlazas == 0) {
                    throw new IllegalStateException("No hay plazas suficientes para aumentar la reserva");
                }
            } else if (delta < 0) {
                cursoFechaDao.sumarPlazas(reserva.getCursoFecha().getId(), -delta);
            }
        }

        reserva.setPlazasReservadas(plazasNuevas);

        CursoCompra actualizada = cursoCompraDao.save(reserva);
        CursoCompraDto dtoActualizada = CursoCompraAssembler.toDto(actualizada);
        logger.info("actualizarProducto - Reserva actualizada con ID: {}", dtoActualizada.getId());
        return dtoActualizada;
    }

    @Transactional
    public void eliminarProducto(Long id) {
        logger.info("eliminarProducto - Intentando eliminar reserva con ID: {}", id);
        CursoCompra reserva = cursoCompraDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva con id " + id + " no encontrada"));

        cursoFechaDao.sumarPlazas(reserva.getCursoFecha().getId(), reserva.getPlazasReservadas());
        cursoCompraDao.deleteById(id);
        logger.info("eliminarProducto - Reserva con ID {} eliminada correctamente (plazas liberadas)", id);
    }
}
