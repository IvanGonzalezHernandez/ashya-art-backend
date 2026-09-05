package com.ashyaart.ashya_art_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.service.ConfiguracionSitioService;

@RestController
@RequestMapping("/api/config")
public class ConfiguracionSitioController {

    @Autowired
    private ConfiguracionSitioService configuracionSitioService;

    // Público: el propio sitio (sin login) necesita saber si debe mostrar mantenimiento.
    @GetMapping("/mantenimiento")
    public ResponseEntity<Map<String, Boolean>> getMantenimiento() {
        return ResponseEntity.ok(Map.of("activo", configuracionSitioService.isMantenimientoActivo()));
    }

    // Privado: solo el admin puede activar/desactivar mantenimiento.
    @PutMapping("/mantenimiento")
    public ResponseEntity<Map<String, Boolean>> setMantenimiento(@RequestBody Map<String, Boolean> body) {
        boolean activo = Boolean.TRUE.equals(body.get("activo"));
        boolean resultado = configuracionSitioService.setMantenimientoActivo(activo);
        return ResponseEntity.ok(Map.of("activo", resultado));
    }
}
