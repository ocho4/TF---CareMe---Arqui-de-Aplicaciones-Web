package com.upc.careme.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private String obtenerFechaFormateada() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errores = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Campos inválidos o incompletos");
        respuesta.put("mensajes", errores);
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CuidadorInactivoException.class)
    public ResponseEntity<Map<String, Object>> manejarCuidadorInactivo(CuidadorInactivoException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("tipo", ex.getTipo());
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.FORBIDDEN.value());
        if (ex.getObservaciones() != null) {
            respuesta.put("observaciones", ex.getObservaciones());
        }
        if (ex.getIdUsuario() != null) {
            respuesta.put("idUsuario", ex.getIdUsuario());
        }
        return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarEntidadNoEncontrada(EntityNotFoundException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarViolacionIntegridad(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Conflicto de integridad en la base de datos");
        respuesta.put("mensaje", "La operación no se pudo completar debido a una restricción en la persistencia (llave duplicada o foránea inválida).");
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorTipoParametro(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Tipo de parámetro incorrecto");
        respuesta.put("mensaje", String.format("El parámetro '%s' recibió un valor inválido. Se esperaba un tipo: %s", ex.getName(), ex.getRequiredType().getSimpleName()));
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroFaltante(MissingServletRequestParameterException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Falta parámetro requerido");
        respuesta.put("mensaje", String.format("El parámetro '%s' es obligatorio para esta solicitud.", ex.getParameterName()));
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Método HTTP no soportado");
        respuesta.put("mensaje", String.format("El método '%s' no está permitido para esta ruta.", ex.getMethod()));
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<>(respuesta, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentosInvalidos(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Datos incorrectos");
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarErroresGenerales(RuntimeException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", obtenerFechaFormateada());
        respuesta.put("error", "Error interno o de negocio");
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("ruta", request.getDescription(false).replace("uri=", ""));
        workspaceFallbackTrace(ex, respuesta);
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void workspaceFallbackTrace(RuntimeException ex, Map<String, Object> respuesta) {
        if (ex.getCause() != null) {
            respuesta.put("causaRaiz", ex.getCause().toString());
        } else {
            respuesta.put("causaRaiz", "No especificada de forma explícita");
        }
    }
}