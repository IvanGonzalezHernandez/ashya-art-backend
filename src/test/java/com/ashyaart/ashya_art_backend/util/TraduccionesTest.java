package com.ashyaart.ashya_art_backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ashyaart.ashya_art_backend.entity.TraduccionesConverter;

class TraduccionesTest {

    @Test
    void soloGuardaIdiomasYCamposConocidosConTexto() {
        Map<String, Map<String, String>> entrada = Map.of(
                "de", Map.of("nombre", "  Kintsugi-Kurs ", "descripcion", "", "precio", "10", "subtitulo", "   "),
                "es", Map.of("nombre", "Clase de kintsugi"),
                "fr", Map.of("nombre", "Cours"));

        Map<String, Map<String, String>> limpio = Traducciones.limpiar(entrada, Traducciones.CAMPOS_CURSO);

        assertEquals(Map.of("nombre", "Kintsugi-Kurs"), limpio.get("de"));
        assertEquals(Map.of("nombre", "Clase de kintsugi"), limpio.get("es"));
        assertEquals(2, limpio.size());
    }

    @Test
    void unIdiomaSinTextosNoSeGuarda() {
        assertTrue(Traducciones.limpiar(Map.of("de", Map.of("nombre", " ")), Traducciones.CAMPOS_TARJETA).isEmpty());
        assertTrue(Traducciones.limpiar(null, Traducciones.CAMPOS_PRODUCTO).isEmpty());
    }

    @Test
    void recortaLosTextosDemasiadoLargos() {
        String largo = "x".repeat(600);
        var limpio = Traducciones.limpiar(Map.of("es", Map.of("nombre", largo)), Traducciones.CAMPOS_PRODUCTO);
        assertEquals(500, limpio.get("es").get("nombre").length());
    }

    @Test
    void elConversorGuardaJsonYToleraDatosDanados() {
        TraduccionesConverter c = new TraduccionesConverter();
        Map<String, Map<String, String>> t = Map.of("de", Map.of("nombre", "Kurs"));

        String json = c.convertToDatabaseColumn(t);
        assertEquals(t, c.convertToEntityAttribute(json));
        assertNull(c.convertToDatabaseColumn(Map.of()));
        assertTrue(c.convertToEntityAttribute(null).isEmpty());
        assertTrue(c.convertToEntityAttribute("{no es json").isEmpty());
    }
}
