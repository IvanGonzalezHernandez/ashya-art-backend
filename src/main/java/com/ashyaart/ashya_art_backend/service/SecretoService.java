package com.ashyaart.ashya_art_backend.service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.SecretoAssembler;
import com.ashyaart.ashya_art_backend.entity.Secreto;
import com.ashyaart.ashya_art_backend.filter.SecretoFilter;
import com.ashyaart.ashya_art_backend.model.SecretoDto;
import com.ashyaart.ashya_art_backend.repository.SecretoDao;

@Service
public class SecretoService {

    private static final Logger logger = LoggerFactory.getLogger(SecretoService.class);

    @Autowired
    private SecretoDao secretoDao;

    public List<SecretoDto> findByFilter(SecretoFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de secretos");
        List<Secreto> secretos = secretoDao.findByFiltros(filter.getNombre());
        List<SecretoDto> resultado = secretos.stream().map(SecretoAssembler::toDto).toList();
        logger.info("findByFilter - Se encontraron {} secretos", resultado.size());
        return resultado;
    }

    public SecretoDto obtenerSecretoPorId(Long id) {
        logger.info("obtenerSecretoPorId - Buscando secreto con ID: {}", id);
        Optional<Secreto> secretoOpt = secretoDao.findById(id);
        return secretoOpt.map(SecretoAssembler::toDto).orElse(null);
    }

    @Transactional
    public SecretoDto crearSecreto(SecretoDto dto) {
        logger.info("crearSecreto - Creando nuevo secreto");
        Secreto entity = SecretoAssembler.toEntity(dto);
        entity.setId(null);
        Secreto guardado = secretoDao.save(entity);
        SecretoDto resp = SecretoAssembler.toDto(guardado);
        logger.info("crearSecreto - Secreto creado ID: {}", resp.getId());
        return resp;
    }

    @Transactional
    public SecretoDto actualizarSecreto(SecretoDto dto) {
        logger.info("actualizarSecreto - Actualizando secreto ID: {}", dto.getId());
        Secreto entity = secretoDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Secreto no encontrado con ID: " + dto.getId()));

        // Campos normales
        entity.setNombre(dto.getNombre());
        entity.setSubtitulo(dto.getSubtitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCategoria(dto.getCategoria());
        entity.setPrecio(dto.getPrecio());
        entity.setEstado(dto.getEstado());
        entity.setFechaBaja(dto.getFechaBaja());

        // Borrados explícitos
        if (Boolean.TRUE.equals(dto.getDeleteImg1())) entity.setImg1(null);
        if (Boolean.TRUE.equals(dto.getDeleteImg2())) entity.setImg2(null);
        if (Boolean.TRUE.equals(dto.getDeleteImg3())) entity.setImg3(null);
        if (Boolean.TRUE.equals(dto.getDeleteImg4())) entity.setImg4(null);
        if (Boolean.TRUE.equals(dto.getDeleteImg5())) entity.setImg5(null);
        if (Boolean.TRUE.equals(dto.getDeletePdf()))  entity.setPdf(null);

        // Reemplazos: si llegaron bytes nuevos tienen prioridad sobre el flag
        if (dto.getImg1() != null) entity.setImg1(dto.getImg1());
        if (dto.getImg2() != null) entity.setImg2(dto.getImg2());
        if (dto.getImg3() != null) entity.setImg3(dto.getImg3());
        if (dto.getImg4() != null) entity.setImg4(dto.getImg4());
        if (dto.getImg5() != null) entity.setImg5(dto.getImg5());
        if (dto.getPdf()  != null) entity.setPdf(dto.getPdf());

        Secreto guardado = secretoDao.save(entity);
        SecretoDto resp = SecretoAssembler.toDto(guardado);
        logger.info("actualizarSecreto - Secreto actualizado ID: {}", resp.getId());
        return resp;
    }

    @Transactional
    public void eliminarSecreto(Long id) {
        logger.info("eliminarSecreto - Intentando eliminar secreto ID: {}", id);
        if (!secretoDao.existsById(id)) {
            logger.warn("eliminarSecreto - Secreto con ID {} no encontrado", id);
            throw new RuntimeException("Secreto con id " + id + " no encontrado");
        }
        Integer filas = secretoDao.borradoLogico(id);
        if (filas == 0) {
            logger.error("eliminarSecreto - No se pudo eliminar el secreto con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar el secreto con id " + id);
        }
        logger.info("eliminarSecreto - Secreto con ID {} eliminado (borrado lógico)", id);
    }
}
