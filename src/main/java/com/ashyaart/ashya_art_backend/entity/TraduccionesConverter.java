package com.ashyaart.ashya_art_backend.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Guarda las traducciones ({ idioma: { campo: texto } }) como JSON en una sola columna. */
@Converter
public class TraduccionesConverter implements AttributeConverter<Map<String, Map<String, String>>, String> {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final TypeReference<LinkedHashMap<String, Map<String, String>>> TIPO = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, Map<String, String>> traducciones) {
        if (traducciones == null || traducciones.isEmpty()) return null;
        try {
            return JSON.writeValueAsString(traducciones);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("No se pudieron guardar las traducciones", e);
        }
    }

    @Override
    public Map<String, Map<String, String>> convertToEntityAttribute(String columna) {
        if (columna == null || columna.isBlank()) return new LinkedHashMap<>();
        try {
            return JSON.readValue(columna, TIPO);
        } catch (JsonProcessingException e) {
            // Un JSON dañado no debe romper la web: se muestra el inglés
            return new LinkedHashMap<>();
        }
    }
}
