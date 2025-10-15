package com.ashyaart.ashya_art_backend.advice;

import com.ashyaart.ashya_art_backend.service.LogErrorService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Captura todas las excepciones no controladas de la aplicación
 * y las guarda en la tabla LOG_ERRORES mediante LogErrorService.
 */
@ControllerAdvice
public class ManejadorErroresGlobal {

    private static final Logger logger = LoggerFactory.getLogger(ManejadorErroresGlobal.class);

    private final LogErrorService logErrorService;

    public ManejadorErroresGlobal(LogErrorService logErrorService) {
        this.logErrorService = logErrorService;
    }

    /**
     * Captura cualquier excepción no controlada en los controladores.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcion(HttpServletRequest request, Exception ex) {

        // 1️ Registrar el error completo en consola
        logger.error("Error no controlado en la petición: {}", request.getRequestURI(), ex);

        // 2️ Guardar el error en la base de datos (asíncrono)
        logErrorService.guardar(
                ex,
                ManejadorErroresGlobal.class.getName(),
                request.getMethod(),
                request.getRequestURI()
        );

        // 3️ Responder al cliente sin exponer detalles internos
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Se ha producido un error interno en el servidor.");
    }
}
