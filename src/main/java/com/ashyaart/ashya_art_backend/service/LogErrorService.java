// src/main/java/com/ashyaart/ashya_art_backend/service/LogErrorService.java
package com.ashyaart.ashya_art_backend.service;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.LogError;
import com.ashyaart.ashya_art_backend.repository.LogErrorDao;

@Service
public class LogErrorService {

    private final LogErrorDao repo;
    private final String entorno;
    private final String servidor;

    public LogErrorService(LogErrorDao repo) {
        this.repo = repo;
        this.entorno = System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE",
                System.getProperty("spring.profiles.active", "local"));
        String h;
        try { h = InetAddress.getLocalHost().getHostName(); } catch (Exception e) { h = "desconocido"; }
        this.servidor = h;
    }

    @Async
    public void guardar(Throwable ex, String logger, String metodoHttp, String ruta) {
        LogError log = new LogError();

        log.setMensajeError(ex.toString());
        if (ex.getStackTrace().length > 0) {
            StackTraceElement top = ex.getStackTrace()[0];
            log.setClaseError(top.getClassName());
            log.setMetodoError(top.getMethodName());
            log.setLineaError(top.getLineNumber());
        }
        log.setLoggerOrigen(logger);
        log.setMetodoHttp(metodoHttp);
        log.setRutaPeticion(ruta);
        log.setEntorno(entorno);
        log.setServidor(servidor);
        log.setHashTraza(hash(ex));

        repo.save(log);
    }

    private String hash(Throwable ex) {
        try {
            StringBuilder sb = new StringBuilder(ex.toString()).append('\n');
            for (int i = 0; i < Math.min(20, ex.getStackTrace().length); i++) {
                sb.append(ex.getStackTrace()[i]).append('\n');
            }
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(sb.toString().getBytes()));
        } catch (Exception e) {
            return null;
        }
    }
}
