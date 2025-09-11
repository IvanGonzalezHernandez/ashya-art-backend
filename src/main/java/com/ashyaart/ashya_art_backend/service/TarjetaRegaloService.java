package com.ashyaart.ashya_art_backend.service;

import com.ashyaart.ashya_art_backend.assembler.TarjetaRegaloAssembler;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloFilter;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloDto;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloDao;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TarjetaRegaloService {

    private static final Logger logger = LoggerFactory.getLogger(TarjetaRegaloService.class);

    @Autowired
    private TarjetaRegaloDao tarjetaRegaloDao;

    @Autowired
    private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;

    public List<TarjetaRegaloDto> findByFilter(TarjetaRegaloFilter filter) {
        List<TarjetaRegalo> tarjetas = tarjetaRegaloDao.findByFiltros(filter.getNombre());
        return tarjetas.stream().map(TarjetaRegaloAssembler::toDto).toList();
    }

    public TarjetaRegaloDto obtenerTarjetaPorId(Long id) {
        Optional<TarjetaRegalo> tarjetaOpt = tarjetaRegaloDao.findById(id);
        return tarjetaOpt.map(TarjetaRegaloAssembler::toDto).orElse(null);
    }

    @Transactional
    public TarjetaRegaloDto crearTarjetaRegalo(TarjetaRegaloDto tarjetaDto) {
        TarjetaRegalo tarjeta = TarjetaRegaloAssembler.toEntity(tarjetaDto);
        tarjeta.setId(null);
        TarjetaRegalo guardada = tarjetaRegaloDao.save(tarjeta);
        return TarjetaRegaloAssembler.toDto(guardada);
    }

    /**
     * Actualiza tarjeta regalo con control de imagen:
     * - Si mustDelete = true -> elimina imagen (setImg(null))
     * - Si nuevaImagen != null -> reemplaza imagen
     * - Si neither -> mantiene imagen actual
     */
    @Transactional
    public TarjetaRegaloDto actualizarTarjetaRegalo(TarjetaRegaloDto dto, byte[] nuevaImagen, boolean mustDelete) {
        TarjetaRegalo tarjeta = tarjetaRegaloDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tarjeta regalo no encontrada con ID: " + dto.getId()));

        // Campos básicos (sin idReferencia)
        tarjeta.setNombre(dto.getNombre());
        tarjeta.setPrecio(dto.getPrecio());
        tarjeta.setEstado(dto.getEstado());
        tarjeta.setFechaAlta(dto.getFechaAlta());
        tarjeta.setFechaBaja(dto.getFechaBaja());

        // Imagen
        if (mustDelete) {
            tarjeta.setImg(null);
        } else if (nuevaImagen != null) {
            tarjeta.setImg(nuevaImagen);
        } // else -> mantener la actual

        TarjetaRegalo actualizada = tarjetaRegaloDao.save(tarjeta);
        return TarjetaRegaloAssembler.toDto(actualizada);
    }

    @Transactional
    public void eliminarTarjetaRegalo(Long id) {
        if (!tarjetaRegaloDao.existsById(id)) {
            throw new EntityNotFoundException("Tarjeta regalo con id " + id + " no encontrada");
        }
        int filas = tarjetaRegaloDao.borradoLogico(id);
        if (filas == 0) {
            throw new RuntimeException("No se pudo eliminar la tarjeta regalo con id " + id);
        }
    }

    public TarjetaRegaloCompra obtenerTarjetaPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        return tarjetaRegaloCompraDao.findByCodigo(codigo.trim().toUpperCase()).orElse(null);
    }
}
