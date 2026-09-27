package com.ashyaart.ashya_art_backend.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Traducciones del contenido escrito desde el admin (cursos, productos, tarjetas regalo).
 * El texto de las columnas normales es la versión en inglés; aquí se guardan el alemán y el
 * español como { "de": { "nombre": "...", ... }, "es": { ... } }. Un campo sin traducir no se
 * guarda y la web muestra el inglés.
 */
public final class Traducciones {

    public static final List<String> IDIOMAS = List.of("de", "es");

    public static final Set<String> CAMPOS_CURSO = Set.of(
            "nombre", "subtitulo", "descripcion", "nivel", "duracion", "piezas", "materiales", "localizacion");
    public static final Set<String> CAMPOS_PRODUCTO = Set.of(
            "nombre", "subtitulo", "descripcion", "categoria", "medidas", "material");
    public static final Set<String> CAMPOS_TARJETA = Set.of("nombre");

    private static final int MAX_DESCRIPCION = 10_000;
    private static final int MAX_CAMPO = 500;

    private Traducciones() {
    }

    /**
     * Deja solo idiomas y campos conocidos, sin espacios sobrantes, sin textos vacíos y con una
     * longitud razonable. Devuelve siempre un mapa (vacío si no hay nada traducido).
     */
    public static Map<String, Map<String, String>> limpiar(Map<String, Map<String, String>> traducciones, Set<String> campos) {
        Map<String, Map<String, String>> limpio = new LinkedHashMap<>();
        if (traducciones == null) return limpio;
        for (String idioma : IDIOMAS) {
            Map<String, String> textos = traducciones.get(idioma);
            if (textos == null) continue;
            Map<String, String> ok = new LinkedHashMap<>();
            for (String campo : campos.stream().sorted().toList()) {
                String texto = textos.get(campo);
                if (texto == null || texto.isBlank()) continue;
                int max = campo.equals("descripcion") ? MAX_DESCRIPCION : MAX_CAMPO;
                String t = texto.strip();
                ok.put(campo, t.length() > max ? t.substring(0, max) : t);
            }
            if (!ok.isEmpty()) limpio.put(idioma, ok);
        }
        return limpio;
    }
}
