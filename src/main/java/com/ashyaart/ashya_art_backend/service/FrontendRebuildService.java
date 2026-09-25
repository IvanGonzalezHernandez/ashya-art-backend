package com.ashyaart.ashya_art_backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

/**
 * El frontend prerenderiza en el build las páginas de productos, cursos y tarjetas regalo.
 * Cuando cambian esos datos (edición desde el admin o stock tras una compra) hay que volver
 * a generar el frontend para que el HTML no quede desfasado: este servicio llama al deploy
 * hook del frontend en Render.
 *
 * Los cambios seguidos se agrupan: cada petición reinicia una espera de {@code frontend.rebuild-delay-seconds},
 * así editar varios productos seguidos lanza un único build.
 * Sin FRONTEND_DEPLOY_HOOK_URL configurada (p.ej. en local) no hace nada.
 */
@Service
public class FrontendRebuildService {

    private static final Logger logger = LoggerFactory.getLogger(FrontendRebuildService.class);

    private final String deployHookUrl;
    private final long delaySeconds;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "frontend-rebuild");
        t.setDaemon(true);
        return t;
    });

    private ScheduledFuture<?> pendiente;

    public FrontendRebuildService(
            @Value("${frontend.deploy-hook-url:}") String deployHookUrl,
            @Value("${frontend.rebuild-delay-seconds:60}") long delaySeconds) {
        this.deployHookUrl = deployHookUrl == null ? "" : deployHookUrl.trim();
        this.delaySeconds = delaySeconds;
    }

    /** Pide regenerar el frontend (agrupando con otras peticiones cercanas). */
    public synchronized void solicitarRebuild(String motivo) {
        if (deployHookUrl.isEmpty()) {
            logger.debug("solicitarRebuild - Sin deploy hook configurado; se ignora ({})", motivo);
            return;
        }
        if (pendiente != null && !pendiente.isDone()) {
            pendiente.cancel(false);
        }
        logger.info("solicitarRebuild - Rebuild del frontend programado en {}s ({})", delaySeconds, motivo);
        pendiente = scheduler.schedule(this::lanzarRebuild, delaySeconds, TimeUnit.SECONDS);
    }

    private void lanzarRebuild() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(deployHookUrl))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() / 100 == 2) {
                logger.info("lanzarRebuild - Rebuild del frontend lanzado (HTTP {})", response.statusCode());
            } else {
                logger.warn("lanzarRebuild - El deploy hook respondió HTTP {}", response.statusCode());
            }
        } catch (Exception e) {
            // Nunca debe afectar a la operación que lo pidió: como mucho el HTML queda desfasado hasta el siguiente build
            logger.warn("lanzarRebuild - No se pudo llamar al deploy hook del frontend: {}", e.getMessage());
        }
    }

    @PreDestroy
    void cerrar() {
        scheduler.shutdownNow();
    }
}
