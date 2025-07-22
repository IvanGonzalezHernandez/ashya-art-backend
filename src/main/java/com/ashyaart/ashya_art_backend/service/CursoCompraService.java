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

        CursoCompra guardado = cursoCompraDao.save(reserva);
        CursoCompraDto dtoGuardado = CursoCompraAssembler.toDto(guardado);
        logger.info("crearProducto - Reserva creada con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public CursoCompraDto actualizarProducto(CursoCompraDto dto) {
        logger.info("actualizarProducto - Actualizando reserva con ID: {}", dto.getId());
        CursoCompra reserva = cursoCompraDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + dto.getId()));

        Cliente cliente = clienteDao.findById(dto.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
        CursoFecha fecha = cursoFechaDao.findById(dto.getIdFecha())
                .orElseThrow(() -> new EntityNotFoundException("CursoFecha no encontrado con ID: " + dto.getIdFecha()));

        reserva.setCliente(cliente);
        reserva.setCursoFecha(fecha);
        reserva.setPlazasReservadas(dto.getPlazasReservadas());
        reserva.setFechaReserva(dto.getFechaReserva());

        CursoCompra actualizada = cursoCompraDao.save(reserva);
        CursoCompraDto dtoActualizada = CursoCompraAssembler.toDto(actualizada);
        logger.info("actualizarProducto - Reserva actualizada con ID: {}", dtoActualizada.getId());
        return dtoActualizada;
    }

    @Transactional
    public void eliminarProducto(Long id) {
        logger.info("eliminarProducto - Intentando eliminar reserva con ID: {}", id);
        if (!cursoCompraDao.existsById(id)) {
            logger.warn("eliminarProducto - Reserva con ID {} no encontrada", id);
            throw new RuntimeException("Reserva con id " + id + " no encontrada");
        }

        cursoCompraDao.deleteById(id);
        logger.info("eliminarProducto - Reserva con ID {} eliminada correctamente", id);
    }
}
