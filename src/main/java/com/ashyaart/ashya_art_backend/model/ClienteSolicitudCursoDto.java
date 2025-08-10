package com.ashyaart.ashya_art_backend.model;

import com.ashyaart.ashya_art_backend.entity.Cliente;

public class ClienteSolicitudCursoDto extends Cliente {

    private String tipoClase;
    private Integer personasInteresadas;
    private String disponibilidad;
    private String preguntasAdicionales;

    public String getTipoClase() {
        return tipoClase;
    }

    public void setTipoClase(String tipoClase) {
        this.tipoClase = tipoClase;
    }

    public Integer getPersonasInteresadas() {
        return personasInteresadas;
    }

    public void setPersonasInteresadas(Integer personasInteresadas) {
        this.personasInteresadas = personasInteresadas;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public String getPreguntasAdicionales() {
        return preguntasAdicionales;
    }

    public void setPreguntasAdicionales(String preguntasAdicionales) {
        this.preguntasAdicionales = preguntasAdicionales;
    }
}
