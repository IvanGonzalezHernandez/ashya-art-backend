package com.ashyaart.ashya_art_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion_sitio")
public class ConfiguracionSitio {

    @Id
    private Long id;

    @Column(name = "mantenimiento_activo", nullable = false)
    private boolean mantenimientoActivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isMantenimientoActivo() { return mantenimientoActivo; }
    public void setMantenimientoActivo(boolean mantenimientoActivo) { this.mantenimientoActivo = mantenimientoActivo; }
}
