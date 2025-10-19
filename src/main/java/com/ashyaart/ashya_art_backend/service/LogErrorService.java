// src/main/java/com/ashyaart/ashya_art_backend/service/LogErrorService.java
package com.ashyaart.ashya_art_backend.service;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.assembler.LogErrorAssembler;
import com.ashyaart.ashya_art_backend.entity.LogError;
import com.ashyaart.ashya_art_backend.filter.LogErrorFilter;
import com.ashyaart.ashya_art_backend.model.LogErrorDto;
import com.ashyaart.ashya_art_backend.repository.LogErrorDao;

@Service
public class LogErrorService {

    private static final Logger log = LoggerFactory.getLogger(LogErrorService.class);

    private final LogErrorDao repo;
    private final String entorno;
    private final String servidor;

    /**
     * entorno.nombre (propiedades) > ENVIRONMENT (var de entorno) > "local"
     *
     * En Render: ENVIRONMENT=pre
     * En local: no pongo nada (quedará "local")
     */
    public LogErrorService(
            LogErrorDao repo,
            @Value("${entorno.nombre:${ENVIRONMENT:local}}") String entorno) {
        this.repo = repo;
        this.entorno = entorno;

        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            host = "desconocido";
        }
        this.servidor = host;
    }

    @Async
    public void guardar(Throwable ex, String loggerOrigen, String metodoHttp, String ruta) {
        LogError le = new LogError();

        // Mensaje completo (toString); si prefieres la traza completa, puedes guardarla también
        le.setMensajeError(ex.toString());

        // Extraer info del primer frame
        if (ex.getStackTrace().length > 0) {
            StackTraceElement top = ex.getStackTrace()[0];
            le.setClaseError(top.getClassName());
            le.setMetodoError(top.getMethodName());
            le.setLineaError(top.getLineNumber());
        }

        le.setLoggerOrigen(loggerOrigen);
        le.setMetodoHttp(metodoHttp);
        le.setRutaPeticion(ruta);
        le.setEntorno(entorno);
        le.setServidor(servidor);
        le.setHashTraza(hash(ex));

        repo.save(le);
    }

    private String hash(Throwable ex) {
        try {
            StringBuilder sb = new StringBuilder(ex.toString()).append('\n');
            for (int i = 0; i < Math.min(20, ex.getStackTrace().length); i++) {
                sb.append(ex.getStackTrace()[i]).append('\n');
            }
            byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    public List<LogErrorDto> findByFilter(LogErrorFilter filter) {
        log.info("findByFilter - Iniciando búsqueda de errores");
        List<LogError> errores = repo.findByFiltros(filter.getFechaCreacionDesde());
        List<LogErrorDto> resultado = errores.stream()
                .map(LogErrorAssembler::toDto)
                .toList();
        log.info("findByFilter - Se encontraron {} errores", resultado.size());
        return resultado;
    }
}
