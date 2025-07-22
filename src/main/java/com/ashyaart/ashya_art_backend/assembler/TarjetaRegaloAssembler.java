package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.model.TarjetaRegaloDto;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;

public class TarjetaRegaloAssembler {

    public static TarjetaRegaloDto toDto(TarjetaRegalo tarjeta) {
        TarjetaRegaloDto dto = new TarjetaRegaloDto();
        dto.setId(tarjeta.getId());
        dto.setNombre(tarjeta.getNombre());
        dto.setPrecio(tarjeta.getPrecio());
        dto.setIdReferencia(tarjeta.getIdReferencia());
        dto.setImg(tarjeta.getImg());
        dto.setFechaAlta(tarjeta.getFechaAlta());
        dto.setFechaBaja(tarjeta.getFechaBaja());
        dto.setEstado(tarjeta.isEstado());
        
        return dto;
    }

    public static TarjetaRegalo toEntity(TarjetaRegaloDto dto) {
        TarjetaRegalo tarjeta = new TarjetaRegalo();
        tarjeta.setId(dto.getId());
        tarjeta.setNombre(dto.getNombre());
        tarjeta.setPrecio(dto.getPrecio());
        tarjeta.setIdReferencia(dto.getIdReferencia());
        tarjeta.setImg(dto.getImg());
        tarjeta.setFechaAlta(dto.getFechaAlta());
        tarjeta.setFechaBaja(dto.getFechaBaja());
        tarjeta.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        
        return tarjeta;
    }
}
