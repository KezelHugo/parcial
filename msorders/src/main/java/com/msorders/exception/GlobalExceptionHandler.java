package com.msorders.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final String TIMESTAMP = "timestamp";
  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put(TIMESTAMP, LocalDateTime.now());
    error.put(ERROR, "Error interno");
    error.put(MESSAGE, ex.getMessage());
    error.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();
    errors.put(TIMESTAMP, LocalDateTime.now());
    errors.put(STATUS, HttpStatus.BAD_REQUEST.value());
    errors.put(ERROR, "Error de validación");

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(err ->
      fieldErrors.put(err.getField(), err.getDefaultMessage())
    );

    errors.put(MESSAGE, fieldErrors);
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put(TIMESTAMP, LocalDateTime.now());
    body.put(STATUS, HttpStatus.BAD_REQUEST.value());
    body.put(ERROR, "Stock insuficiente");
    body.put(MESSAGE, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }
}
