package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.assembler.SecretoCompraAssembler;
import com.ashyaart.ashya_art_backend.controller.SecretoCompraController;
import com.ashyaart.ashya_art_backend.entity.SecretoCompra;
import com.ashyaart.ashya_art_backend.filter.SecretoCompraFilter;
import com.ashyaart.ashya_art_backend.model.SecretoCompraDto;
import com.ashyaart.ashya_art_backend.repository.SecretoCompraDao;

@Service
public class SecretoCompraService {
	
	@Autowired
	private SecretoCompraDao secretoCompraDao;
	
	private static final Logger logger = LoggerFactory.getLogger(SecretoCompraController.class);
	
    public List<SecretoCompraDto> findByFilter(SecretoCompraFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de clientes");
        List<SecretoCompra> secretos = secretoCompraDao.findByFiltros(filter.getNombre());
        List<SecretoCompraDto> resultado = secretos.stream()
                .map(SecretoCompraAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} secretos", resultado.size());
        return resultado;
    }

}
