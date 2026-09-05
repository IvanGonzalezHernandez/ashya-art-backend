package com.ashyaart.ashya_art_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.ConfiguracionSitio;
import com.ashyaart.ashya_art_backend.repository.ConfiguracionSitioDao;

@Service
public class ConfiguracionSitioService {

    // Fila única: la configuración del sitio no necesita más de un registro.
    private static final Long ID_UNICO = 1L;

    @Autowired
    private ConfiguracionSitioDao configuracionSitioDao;

    public boolean isMantenimientoActivo() {
        return obtenerConfiguracion().isMantenimientoActivo();
    }

    public boolean setMantenimientoActivo(boolean activo) {
        ConfiguracionSitio config = obtenerConfiguracion();
        config.setMantenimientoActivo(activo);
        return configuracionSitioDao.save(config).isMantenimientoActivo();
    }

    private ConfiguracionSitio obtenerConfiguracion() {
        return configuracionSitioDao.findById(ID_UNICO).orElseGet(() -> {
            ConfiguracionSitio nueva = new ConfiguracionSitio();
            nueva.setId(ID_UNICO);
            nueva.setMantenimientoActivo(false);
            return configuracionSitioDao.save(nueva);
        });
    }
}
