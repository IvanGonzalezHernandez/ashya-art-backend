package com.ashyaart.ashya_art_backend.assembler;

import com.ashyaart.ashya_art_backend.entity.LogError;
import com.ashyaart.ashya_art_backend.model.LogErrorDto;

public class LogErrorAssembler {

    public static LogErrorDto toDto(LogError logError) {
        LogErrorDto dto = new LogErrorDto();
        dto.setId(logError.getId());
        dto.setFechaCreacion(logError.getFechaCreacion());
        dto.setMensajeError(logError.getMensajeError());
        dto.setClaseError(logError.getClaseError());
        dto.setMetodoError(logError.getMetodoError());
        dto.setLineaError(logError.getLineaError());
        dto.setLoggerOrigen(logError.getLoggerOrigen());
        dto.setMetodoHttp(logError.getMetodoHttp());
        dto.setRutaPeticion(logError.getRutaPeticion());
        dto.setEntorno(logError.getEntorno());
        dto.setServidor(logError.getServidor());
        dto.setHashTraza(logError.getHashTraza());
        return dto;
    }

    public static LogError toEntity(LogErrorDto dto) {
        LogError logError = new LogError();
        logError.setId(dto.getId());
        logError.setFechaCreacion(dto.getFechaCreacion());
        logError.setMensajeError(dto.getMensajeError());
        logError.setClaseError(dto.getClaseError());
        logError.setMetodoError(dto.getMetodoError());
        logError.setLineaError(dto.getLineaError());
        logError.setLoggerOrigen(dto.getLoggerOrigen());
        logError.setMetodoHttp(dto.getMetodoHttp());
        logError.setRutaPeticion(dto.getRutaPeticion());
        logError.setEntorno(dto.getEntorno());
        logError.setServidor(dto.getServidor());
        logError.setHashTraza(dto.getHashTraza());
        return logError;
    }
}
