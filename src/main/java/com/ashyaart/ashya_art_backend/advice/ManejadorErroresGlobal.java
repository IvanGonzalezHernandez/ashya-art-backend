package com.ashyaart.ashya_art_backend.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Captura todas las excepciones no controladas de la aplicación
 * y devuelve una respuesta HTTP limpia en lugar de la página de error por defecto.
 */
@ControllerAdvice
public class ManejadorErroresGlobal {

    private static final Logger logger = LoggerFactory.getLogger(ManejadorErroresGlobal.class);

    /**
     * Maneja ResponseStatusException (como tu 409 de newsletter) respetando el status.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> manejarResponseStatus(HttpServletRequest request, ResponseStatusException ex) {

        logger.warn("ResponseStatusException en la petición {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getReason()); // "This email is already subscribed."
    }

    /**
     * Captura cualquier otra excepción no controlada en los controladores.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcion(HttpServletRequest request, Exception ex) {

        logger.error("Error no controlado en la petición: {}", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Se ha producido un error interno en el servidor.");
    }
}
