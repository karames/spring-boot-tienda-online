package com.ejemplo.tienda_online.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación
 * Centraliza el manejo de errores y proporciona respuestas consistentes
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS",
                "Credenciales inválidas"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "No tienes permisos para acceder a este recurso"
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        String reason = ex.getReason();
        return buildErrorResponse(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                reason != null ? reason.toUpperCase().replace(" ", "_") : "ERROR",
                reason != null ? reason : "Error en la solicitud"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                ex.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Error interno del servidor"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UNKNOWN_ERROR",
                "Error inesperado en el servidor"
        );
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String errorCode,
            String message
    ) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", status.value());
        errorBody.put("error", errorCode);
        errorBody.put("message", message);
        errorBody.put("path", "");

        return ResponseEntity.status(status).body(errorBody);
    }
}
