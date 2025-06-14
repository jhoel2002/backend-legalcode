package com.application.claimhereweb.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.application.claimhereweb.exceptions.ResourceNotFoundException;
import com.application.claimhereweb.model.entity.ErrorEntity;

@RestControllerAdvice
public class HandlerExceptionController {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorEntity> notFoundEx(NoHandlerFoundException e) {
        ErrorEntity error = new ErrorEntity();
        error.setDate(new Date());
        error.setError("Api rest no encontrado");
        error.setMessage(e.getMessage());

        error.setStatus(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("date", new Date());
        error.put("error", "Recurso no encontrado");
        error.put("message", ex.getMessage());
        error.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();

        Map<String, String> fieldErrors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            fieldErrors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("date", new Date());
        errorResponse.put("error", "Validación de campos fallida");
        errorResponse.put("message", "Se encontraron errores en los campos enviados");
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorEntity> handleDuplicateEmail(ResponseStatusException ex) {
        ErrorEntity error = new ErrorEntity();
        error.setError("Duplicate");
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.CONFLICT.value()); // 409
        error.setDate(new Date());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // @ExceptionHandler({DataAccessException.class,
    // ConstraintViolationException.class})
    // public ResponseEntity<?> handleCaseSaveException(DataAccessException ex) {
    // Map<String, Object> error = new HashMap<>();
    // error.put("date", new Date());
    // error.put("error", "Error al guardar la entidad");
    // error.put("message", ex.getMessage());
    // error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    // }
}
