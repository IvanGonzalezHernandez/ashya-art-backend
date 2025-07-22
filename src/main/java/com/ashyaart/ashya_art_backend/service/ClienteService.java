package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.ClienteAssembler;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.filter.ClienteFilter;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.repository.ClienteDao;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteDao clienteDao;

    public List<ClienteDto> findByFilter(ClienteFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de clientes");
        List<Cliente> clientes = clienteDao.findByFiltros(filter.getNombre());
        List<ClienteDto> resultado = clientes.stream()
                .map(ClienteAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} clientes", resultado.size());
        return resultado;
    }

    @Transactional
    public ClienteDto crearCliente(ClienteDto clienteDto) {
        logger.info("crearCliente - Creando nuevo cliente: {}", clienteDto);
        Cliente cliente = ClienteAssembler.toEntity(clienteDto);
        cliente.setId(null);
        Cliente clienteGuardado = clienteDao.save(cliente);
        ClienteDto dtoGuardado = ClienteAssembler.toDto(clienteGuardado);
        logger.info("crearCliente - Cliente creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public ClienteDto actualizarCliente(ClienteDto clienteDto) {
        logger.info("actualizarCliente - Actualizando cliente con ID: {}", clienteDto.getId());
        Cliente cliente = clienteDao.findById(clienteDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + clienteDto.getId()));

        cliente.setNombre(clienteDto.getNombre());
        cliente.setApellido(clienteDto.getApellido());
        cliente.setCalle(clienteDto.getCalle());
        cliente.setNumero(clienteDto.getNumero());
        cliente.setPiso(clienteDto.getPiso());
        cliente.setCiudad(clienteDto.getCiudad());
        cliente.setCodigoPostal(clienteDto.getCodigoPostal());
        cliente.setProvincia(clienteDto.getProvincia());
        cliente.setTelefono(clienteDto.getTelefono());
        cliente.setEmail(clienteDto.getEmail());
        cliente.setFechaAlta(clienteDto.getFechaAlta());
        cliente.setFechaBaja(clienteDto.getFechaBaja());

        Cliente clienteActualizado = clienteDao.save(cliente);
        ClienteDto dtoActualizado = ClienteAssembler.toDto(clienteActualizado);
        logger.info("actualizarCliente - Cliente actualizado con ID: {}", dtoActualizado.getId());
        return dtoActualizado;
    }

    @Transactional
    public void eliminarCliente(Long id) {
        logger.info("eliminarCliente - Intentando eliminar cliente con ID: {}", id);
        if (!clienteDao.existsById(id)) {
            logger.warn("eliminarCliente - Cliente con ID {} no encontrado", id);
            throw new RuntimeException("Cliente con id " + id + " no encontrado");
        }
        Integer filasAfectadas = clienteDao.borradoLogico(id);
        if (filasAfectadas == 0) {
            logger.error("eliminarCliente - No se pudo eliminar el cliente con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar el cliente con id " + id);
        }
        logger.info("eliminarCliente - Cliente con ID {} eliminado correctamente (borrado lógico)", id);
    }
}
