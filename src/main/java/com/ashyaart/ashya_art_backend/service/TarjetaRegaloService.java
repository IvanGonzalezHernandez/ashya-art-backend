package com.ashyaart.ashya_art_backend.service;

import com.ashyaart.ashya_art_backend.assembler.TarjetaRegaloAssembler;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;
import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloFilter;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloDto;
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

    public List<TarjetaRegaloDto> findByFilter(TarjetaRegaloFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de tarjetas regalo");
        List<TarjetaRegalo> tarjetas = tarjetaRegaloDao.findByFiltros(filter.getNombre());
        List<TarjetaRegaloDto> resultado = tarjetas.stream()
                .map(TarjetaRegaloAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} tarjetas regalo", resultado.size());
        return resultado;
    }
    
    public TarjetaRegaloDto obtenerTarjetaPorId(Long id) {
        logger.info("obtenerTarjetaPorId - Buscando tarjeta regalo con ID: {}", id);
        Optional<TarjetaRegalo> tarjetaOpt = tarjetaRegaloDao.findById(id);

        if (tarjetaOpt.isPresent()) {
            TarjetaRegaloDto dto = TarjetaRegaloAssembler.toDto(tarjetaOpt.get());
            logger.info("obtenerTarjetaPorId - Tarjeta encontrada con ID: {}", id);
            return dto;
        } else {
            logger.warn("obtenerTarjetaPorId - Tarjeta con ID {} no encontrada", id);
            return null;
        }
    }


    @Transactional
    public TarjetaRegaloDto crearTarjetaRegalo(TarjetaRegaloDto tarjetaDto) {
        logger.info("crearTarjetaRegalo - Creando nueva tarjeta regalo: {}", tarjetaDto);
        TarjetaRegalo tarjeta = TarjetaRegaloAssembler.toEntity(tarjetaDto);
        tarjeta.setId(null);
        TarjetaRegalo guardada = tarjetaRegaloDao.save(tarjeta);
        TarjetaRegaloDto dtoGuardado = TarjetaRegaloAssembler.toDto(guardada);
        logger.info("crearTarjetaRegalo - Tarjeta regalo creada con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public TarjetaRegaloDto actualizarTarjetaRegalo(TarjetaRegaloDto tarjetaDto) {
        logger.info("actualizarTarjetaRegalo - Actualizando tarjeta regalo con ID: {}", tarjetaDto.getId());
        TarjetaRegalo tarjeta = tarjetaRegaloDao.findById(tarjetaDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tarjeta regalo no encontrada con ID: " + tarjetaDto.getId()));

        tarjeta.setNombre(tarjetaDto.getNombre());
        tarjeta.setIdReferencia(tarjetaDto.getIdReferencia());
        tarjeta.setPrecio(tarjetaDto.getPrecio());
        tarjeta.setImg(tarjetaDto.getImg());
        tarjeta.setEstado(tarjetaDto.getEstado());
        tarjeta.setFechaAlta(tarjetaDto.getFechaAlta());
        tarjeta.setFechaBaja(tarjetaDto.getFechaBaja());

        TarjetaRegalo actualizada = tarjetaRegaloDao.save(tarjeta);
        TarjetaRegaloDto dtoActualizado = TarjetaRegaloAssembler.toDto(actualizada);
        logger.info("actualizarTarjetaRegalo - Tarjeta regalo actualizada con ID: {}", dtoActualizado.getId());
        return dtoActualizado;
    }

    @Transactional
    public void eliminarTarjetaRegalo(Long id) {
        logger.info("eliminarTarjetaRegalo - Intentando eliminar tarjeta regalo con ID: {}", id);
        if (!tarjetaRegaloDao.existsById(id)) {
            logger.warn("eliminarTarjetaRegalo - Tarjeta regalo con ID {} no encontrada", id);
            throw new RuntimeException("Tarjeta regalo con id " + id + " no encontrada");
        }
        int filas = tarjetaRegaloDao.borradoLogico(id);
        if (filas == 0) {
            logger.error("eliminarTarjetaRegalo - No se pudo eliminar la tarjeta regalo con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar la tarjeta regalo con id " + id);
        }
        logger.info("eliminarTarjetaRegalo - Tarjeta regalo con ID {} eliminada correctamente (borrado lógico)", id);
    }
}
