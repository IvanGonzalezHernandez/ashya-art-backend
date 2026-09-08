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
}
