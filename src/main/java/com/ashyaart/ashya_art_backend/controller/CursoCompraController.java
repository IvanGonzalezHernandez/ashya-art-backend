package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import com.ashyaart.ashya_art_backend.model.CursoCompraDto;
import com.ashyaart.ashya_art_backend.filter.CursoCompraFilter;
import com.ashyaart.ashya_art_backend.service.CursoCompraService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cursos-compra")
public class CursoCompraController {

    private static final Logger logger = LoggerFactory.getLogger(CursoCompraController.class);

    @Autowired
    private CursoCompraService cursoCompraService;

    @GetMapping
    public ResponseEntity<List<CursoCompraDto>> findByFilter(CursoCompraFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener reservas");
        List<CursoCompraDto> reservas = cursoCompraService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} reservas", reservas.size());
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity<CursoCompraDto> crearProducto(@RequestBody CursoCompraDto reservaDto) {
        logger.info("crearProducto - Solicitud POST para crear una nueva reserva: {}", reservaDto);
        CursoCompraDto nuevaReserva = cursoCompraService.crearProducto(reservaDto);
        logger.info("crearProducto - Reserva creada con ID: {}", nuevaReserva.getId());
        return ResponseEntity.ok(nuevaReserva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoCompraDto> actualizarProducto(@PathVariable Long id, @RequestBody CursoCompraDto reservaDto) {
        logger.info("actualizarProducto - Solicitud PUT para actualizar reserva con ID {}: {}", id, reservaDto);
        reservaDto.setId(id);
        CursoCompraDto reservaActualizada = cursoCompraService.actualizarProducto(reservaDto);
        logger.info("actualizarProducto - Reserva actualizada con ID: {}", reservaActualizada.getId());
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("eliminarProducto - Solicitud DELETE para eliminar reserva con ID: {}", id);
        cursoCompraService.eliminarProducto(id);
        logger.info("eliminarProducto - Reserva con ID {} eliminada correctamente", id);
        return ResponseEntity.noContent().build();
    }
}
