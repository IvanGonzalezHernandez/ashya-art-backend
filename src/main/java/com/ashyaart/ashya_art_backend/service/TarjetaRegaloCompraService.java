package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.TarjetaRegaloCompraAssembler;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloCompraFilter;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloCompraDto;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;

@Service
public class TarjetaRegaloCompraService {

    private static final Logger logger = LoggerFactory.getLogger(TarjetaRegaloCompraService.class);

    @Autowired
    private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;

    public List<TarjetaRegaloCompraDto> findByFilter(TarjetaRegaloCompraFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de compras de tarjetas regalo");
        List<TarjetaRegaloCompra> compras = tarjetaRegaloCompraDao.findByFilter(filter.getIdCliente());

        List<TarjetaRegaloCompraDto> resultado = compras.stream()
                .map(TarjetaRegaloCompraAssembler::toDto)
                .toList();

        logger.info("findByFilter - Se encontraron {} compras de tarjetas regalo", resultado.size());
        return resultado;
    }

    @Transactional
    public void canjear(Long id) {
        logger.info("canjear - Intentando canjear compra ID {}", id);

        // Validación de existencia
        TarjetaRegaloCompra compra = tarjetaRegaloCompraDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra de tarjeta regalo no encontrada con ID: " + id));

        if (compra.isCanjeada()) {
            logger.info("canjear - La compra ID {} ya estaba canjeada. No se realizan cambios.", id);
            return;
        }

        int actualizados = tarjetaRegaloCompraDao.canjearPorId(id);
        if (actualizados == 0) {
            // Caso raro: condición de carrera o no cumple el WHERE
            throw new IllegalStateException("No se pudo canjear la compra con ID: " + id);
        }
        logger.info("canjear - Compra ID {} canjeada correctamente", id);
    }

    @Transactional
    public void eliminarLogico(Long id) {
        logger.info("eliminarLogico - Intentando desactivar (borrado lógico) compra ID {}", id);

        if (!tarjetaRegaloCompraDao.existsById(id)) {
            logger.warn("eliminarLogico - Compra con ID {} no encontrada", id);
            throw new EntityNotFoundException("Compra de tarjeta regalo no encontrada con ID: " + id);
        }

        int actualizados = tarjetaRegaloCompraDao.desactivarPorId(id);
        if (actualizados == 0) {
            throw new IllegalStateException("No se pudo desactivar la compra con ID: " + id);
        }
        logger.info("eliminarLogico - Compra ID {} desactivada correctamente", id);
    }
}
