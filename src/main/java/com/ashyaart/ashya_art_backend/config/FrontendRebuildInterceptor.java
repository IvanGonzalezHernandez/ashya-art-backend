package com.ashyaart.ashya_art_backend.config;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ashyaart.ashya_art_backend.service.FrontendRebuildService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tras una escritura correcta desde el admin sobre productos, cursos, tarjetas regalo o
 * compras de producto (que ajustan stock), pide regenerar el frontend prerenderizado.
 */
@Component
public class FrontendRebuildInterceptor implements HandlerInterceptor {

    private static final Set<String> METODOS_ESCRITURA = Set.of("POST", "PUT", "PATCH", "DELETE");

    private static final Pattern RUTAS = Pattern.compile("^/api/(productos|cursos|tarjetas-regalo|productos-compra)(/.*)?$");

    // Endpoints públicos de esas rutas que no modifican nada de lo prerenderizado
    private static final Set<String> EXCLUIDAS = Set.of("/api/cursos/solicitud-curso", "/api/tarjetas-regalo/validar");

    private final FrontendRebuildService frontendRebuildService;

    public FrontendRebuildInterceptor(FrontendRebuildService frontendRebuildService) {
        this.frontendRebuildService = frontendRebuildService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String ruta = request.getRequestURI();
        if (ex == null
                && response.getStatus() / 100 == 2
                && METODOS_ESCRITURA.contains(request.getMethod())
                && RUTAS.matcher(ruta).matches()
                && !EXCLUIDAS.contains(ruta)) {
            frontendRebuildService.solicitarRebuild(request.getMethod() + " " + ruta);
        }
    }
}
