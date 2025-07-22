package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.entity.Cliente;

public class ClienteAssembler {

    public static ClienteDto toDto(Cliente cliente) {
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setCalle(cliente.getCalle());
        dto.setNumero(cliente.getNumero());
        dto.setPiso(cliente.getPiso());
        dto.setCiudad(cliente.getCiudad());
        dto.setCodigoPostal(cliente.getCodigoPostal());
        dto.setProvincia(cliente.getProvincia());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setFechaAlta(cliente.getFechaAlta());
        dto.setFechaBaja(cliente.getFechaBaja());
        return dto;
    }

    public static Cliente toEntity(ClienteDto dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setCalle(dto.getCalle());
        cliente.setNumero(dto.getNumero());
        cliente.setPiso(dto.getPiso());
        cliente.setCiudad(dto.getCiudad());
        cliente.setCodigoPostal(dto.getCodigoPostal());
        cliente.setProvincia(dto.getProvincia());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setFechaAlta(dto.getFechaAlta());
        cliente.setFechaBaja(dto.getFechaBaja());
        return cliente;
    }
}
