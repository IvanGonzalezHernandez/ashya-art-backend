package com.ashyaart.ashya_art_backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.model.EstadisticasDto;
import com.ashyaart.ashya_art_backend.model.VisitaDto;
import com.ashyaart.ashya_art_backend.service.EstadisticaService;

@RestController
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    /** Público: la web avisa de cada página vista. Siempre responde 204, se cuente o no. */
    @PostMapping("/api/estadisticas/visita")
    public ResponseEntity<Void> registrarVisita(@RequestBody(required = false) VisitaDto visita,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        estadisticaService.registrarVisita(visita, userAgent, esAdmin);
        return ResponseEntity.noContent().build();
    }

    /** Admin: resumen de la sección Statistics (periodo 7d, 30d o 12m). */
    @GetMapping("/api/admin/estadisticas")
    public EstadisticasDto resumen(@RequestParam(defaultValue = "30d") String periodo) {
        return estadisticaService.resumen(periodo);
    }
}
