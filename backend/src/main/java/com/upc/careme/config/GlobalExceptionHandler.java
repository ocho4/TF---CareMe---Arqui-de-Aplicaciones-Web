package com.upc.careme.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Campos inválidos o incompletos");
        respuesta.put("mensajes", errores);
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CuidadorInactivoException.class)
    public ResponseEntity<Map<String, Object>> manejarCuidadorInactivo(CuidadorInactivoException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("tipo", ex.getTipo());
        respuesta.put("mensaje", ex.getMessage());
        if (ex.getObservaciones() != null) respuesta.put("observaciones", ex.getObservaciones());
        if (ex.getIdUsuario() != null) respuesta.put("idUsuario", ex.getIdUsuario());
        return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentosInvalidos(IllegalArgumentException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Datos incorrectos");
        respuesta.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarErroresGenerales(RuntimeException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Error interno o de negocio");
        respuesta.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}