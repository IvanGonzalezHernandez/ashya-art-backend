package com.ashyaart.ashya_art_backend.advice;

import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
     * Peticiones a recursos estáticos inexistentes (/robots.txt, /favicon.ico, etc.,
     * pedidos automáticamente por navegadores y crawlers). No es un error real de la
     * aplicación, así que se registra en debug y se devuelve un 404 limpio en vez de
     * caer en el manejador genérico como un 500.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> manejarRecursoNoEncontrado(HttpServletRequest request, NoResourceFoundException ex) {

        logger.debug("Recurso no encontrado: {}", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Recurso no encontrado.");
    }

    /**
     * Datos que no cumplen las restricciones de la entidad (campos obligatorios vacíos,
     * precio <= 0, etc.), detectados por Bean Validation al persistir. Antes caía en el
     * manejador genérico como un 500; ahora se responde con un 400 y el detalle de qué
     * campos fallaron.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> manejarViolacionRestricciones(HttpServletRequest request, ConstraintViolationException ex) {

        logger.warn("Datos inválidos en la petición {}: {}", request.getRequestURI(), ex.getMessage());

        String detalle = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Datos inválidos: " + detalle);
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
