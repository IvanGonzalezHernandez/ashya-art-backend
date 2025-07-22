package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import com.ashyaart.ashya_art_backend.filter.ClienteFilter;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.service.ClienteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDto>> findByFilter(ClienteFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener clientes");
        List<ClienteDto> clientesDto = clienteService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} clientes", clientesDto.size());
        return ResponseEntity.ok(clientesDto);
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crearCliente(@RequestBody ClienteDto clienteDto) {
        logger.info("crearCliente - Solicitud POST para crear un nuevo cliente: {}", clienteDto);
        ClienteDto nuevoCliente = clienteService.crearCliente(clienteDto);
        logger.info("crearCliente - Cliente creado con ID: {}", nuevoCliente.getId());
        return ResponseEntity.ok(nuevoCliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> actualizarCliente(@PathVariable Long id, @RequestBody ClienteDto clienteDto) {
        logger.info("actualizarCliente - Solicitud PUT para actualizar cliente con ID {}: {}", id, clienteDto);
        clienteDto.setId(id);
        ClienteDto clienteActualizado = clienteService.actualizarCliente(clienteDto);
        logger.info("actualizarCliente - Cliente actualizado con ID: {}", clienteActualizado.getId());
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        logger.info("eliminarCliente - Solicitud DELETE para eliminar cliente con ID: {}", id);
        clienteService.eliminarCliente(id);
        logger.info("eliminarCliente - Cliente con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
