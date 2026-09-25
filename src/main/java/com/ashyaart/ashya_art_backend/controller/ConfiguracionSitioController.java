package com.ashyaart.ashya_art_backend.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Value("${mantenimiento.password:}")
    private String passwordMantenimiento;

    // Público: el propio sitio (sin login) necesita saber si debe mostrar mantenimiento.
    @GetMapping("/mantenimiento")
    public ResponseEntity<Map<String, Boolean>> getMantenimiento() {
        return ResponseEntity.ok(Map.of("activo", configuracionSitioService.isMantenimientoActivo()));
    }

    // Público: comprueba la contraseña de previsualización durante el mantenimiento.
    // Se valida aquí para que la contraseña no tenga que viajar en el bundle del frontend.
    @PostMapping("/mantenimiento/desbloquear")
    public ResponseEntity<Map<String, Boolean>> desbloquearMantenimiento(@RequestBody Map<String, String> body) {
        boolean valida = !passwordMantenimiento.isEmpty()
                && MessageDigest.isEqual(
                        passwordMantenimiento.getBytes(StandardCharsets.UTF_8),
                        body.getOrDefault("password", "").getBytes(StandardCharsets.UTF_8));
        return valida
                ? ResponseEntity.ok(Map.of("valida", true))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valida", false));
    }

    // Privado: solo el admin puede activar/desactivar mantenimiento.
    @PutMapping("/mantenimiento")
    public ResponseEntity<Map<String, Boolean>> setMantenimiento(@RequestBody Map<String, Boolean> body) {
        boolean activo = Boolean.TRUE.equals(body.get("activo"));
        boolean resultado = configuracionSitioService.setMantenimientoActivo(activo);
        return ResponseEntity.ok(Map.of("activo", resultado));
    }
}
