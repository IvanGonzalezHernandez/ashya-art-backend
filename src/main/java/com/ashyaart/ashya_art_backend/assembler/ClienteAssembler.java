package com.ashyaart.ashya_art_backend.assembler;

import java.util.ArrayList;
import java.util.List;

import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.CursoCompra;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.model.CursoClienteDto;
import com.ashyaart.ashya_art_backend.model.TarjetaClienteDto;

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
        dto.setPais(cliente.getPais());
        dto.setCodigoPostal(cliente.getCodigoPostal());
        dto.setProvincia(cliente.getProvincia());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setFechaAlta(cliente.getFechaAlta());
        dto.setFechaBaja(cliente.getFechaBaja());
        
        if (cliente.getComprasCursos() != null) {
            List<CursoClienteDto> cursos = new ArrayList<>();

            for (CursoCompra compra : cliente.getComprasCursos()) {
                CursoClienteDto cursoDto = new CursoClienteDto();

                if (compra.getCursoFecha() != null) {
                    if (compra.getCursoFecha().getCurso() != null) {
                        cursoDto.setCurso(compra.getCursoFecha().getCurso().getNombre());
                    }

                    if (compra.getCursoFecha().getFecha() != null) {
                        cursoDto.setFechaCurso(compra.getCursoFecha().getFecha().toString());
                    }
                }

                if (compra.getFechaReserva() != null) {
                    cursoDto.setFechaReserva(compra.getFechaReserva().toString());
                }

                cursoDto.setPlazasReservadas(compra.getPlazasReservadas());

                cursos.add(cursoDto);
            }

            dto.setCursos(cursos);
        }
        
        
        if (cliente.getComprasTarjetas() != null) {
            List<TarjetaClienteDto> tarjetas = new ArrayList<>();

            for (TarjetaRegaloCompra compraTarjeta : cliente.getComprasTarjetas()) {
                TarjetaClienteDto tarjetaDto = new TarjetaClienteDto();

                if (compraTarjeta.getTarjetaRegalo() != null) {
                    tarjetaDto.setTarjeta(compraTarjeta.getTarjetaRegalo().getNombre());
                }

                tarjetaDto.setCanjeada(compraTarjeta.isCanjeada());

                if (compraTarjeta.getFechaCompra() != null) {
                    tarjetaDto.setFechaCompra(compraTarjeta.getFechaCompra().toString());
                }

                if (compraTarjeta.getFechaCaducidad() != null) {
                    tarjetaDto.setFechaCaducidad(compraTarjeta.getFechaCaducidad().toString());
                }

                tarjetas.add(tarjetaDto);
            }

            dto.setTarjetas(tarjetas);
        }

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
        cliente.setPais(dto.getPais());
        cliente.setCodigoPostal(dto.getCodigoPostal());
        cliente.setProvincia(dto.getProvincia());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setFechaAlta(dto.getFechaAlta());
        cliente.setFechaBaja(dto.getFechaBaja());
        return cliente;
    }
}
