package com.ashyaart.ashya_art_backend.model;

/** Cuerpo de PUT /api/cursos-compra/{id}/fecha: la nueva fecha (CursoFecha) a la que se mueve la reserva. */
public class CursoCompraCambioFechaDto {

    private Long idFecha;

    public Long getIdFecha() {
        return idFecha;
    }

    public void setIdFecha(Long idFecha) {
        this.idFecha = idFecha;
    }
}
