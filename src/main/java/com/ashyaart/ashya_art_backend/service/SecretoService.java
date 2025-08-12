package com.ashyaart.ashya_art_backend.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.assembler.ProductoAssembler;
import com.ashyaart.ashya_art_backend.assembler.SecretoAssembler;
import com.ashyaart.ashya_art_backend.controller.SecretoController;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.entity.Secreto;
import com.ashyaart.ashya_art_backend.filter.SecretoFilter;
import com.ashyaart.ashya_art_backend.model.ProductoDto;
import com.ashyaart.ashya_art_backend.model.SecretoDto;
import com.ashyaart.ashya_art_backend.repository.SecretoDao;

@Service
public class SecretoService {
		
	private static final Logger logger = LoggerFactory.getLogger(SecretoController.class);
	
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

        if (secretoOpt.isPresent()) {
            SecretoDto dto = SecretoAssembler.toDto(secretoOpt.get());
            logger.info("obtenerSecretoPorId - Secreto encontrado con ID: {}", id);
            return dto;
        } else {
            logger.warn("obtenerSecretoPorId - Secreto con ID {} no encontrado", id);
            return null;
        }
    }


}
