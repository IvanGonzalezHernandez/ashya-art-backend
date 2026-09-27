package com.ashyaart.ashya_art_backend.model;

import java.util.List;

/** Resumen de la sección Statistics del panel para un periodo (7d, 30d o 12m). */
public record EstadisticasDto(
        String periodo,
        List<String> etiquetas,
        List<Integer> serieActual,
        List<Integer> serieAnterior,
        int visitas,
        int visitasAnterior,
        int paginasVistas,
        int reservas,
        int reservasAnterior,
        int vistasTalleres,
        int desdeGoogle,
        List<Taller> talleres,
        List<Cuenta> paginas,
        List<Cuenta> origenes,
        List<Cuenta> dispositivos,
        List<Cuenta> idiomas) {

    public record Taller(Long id, String nombre, int vistas, int reservas) {
    }

    /** Una fila "clave → número" (página, origen, dispositivo o idioma). */
    public record Cuenta(String clave, int visitas) {
    }
}
