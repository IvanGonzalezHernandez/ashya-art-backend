package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloCompraFilter;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloCompraDto;
import com.ashyaart.ashya_art_backend.service.TarjetaRegaloCompraService;

@RestController
@RequestMapping("/api/tarjetas-regalo-compra")
public class TarjetaRegaloCompraController {

    @Autowired
    TarjetaRegaloCompraService tarjetaRegaloCompraService;

    private static final Logger logger = LoggerFactory.getLogger(TarjetaRegaloCompraController.class);

    @GetMapping
    public ResponseEntity<List<TarjetaRegaloCompraDto>> findByFilter(TarjetaRegaloCompraFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener compras de tarjetas regalo");
        List<TarjetaRegaloCompraDto> comprasDto = tarjetaRegaloCompraService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} compras de tarjetas regalo", comprasDto.size());
        return ResponseEntity.ok(comprasDto);
    }

    @PutMapping("/{id}/canjear")
    public ResponseEntity<Void> canjear(@PathVariable Long id) {
        logger.info("canjear - Solicitud PUT para canjear tarjeta regalo compra con ID: {}", id);
        tarjetaRegaloCompraService.canjear(id);
        logger.info("canjear - Tarjeta regalo compra con ID {} canjeada correctamente", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        logger.info("eliminarLogico - Solicitud DELETE (borrado lógico) para ID: {}", id);
        tarjetaRegaloCompraService.eliminarLogico(id);
        logger.info("eliminarLogico - Tarjeta regalo compra con ID {} desactivada (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
