package com.ashyaart.ashya_art_backend.filter;

import java.time.LocalDate;

public class CursoFechaFilter {
    private LocalDate fecha;

    public CursoFechaFilter() {}

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
