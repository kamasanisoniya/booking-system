package com.example.booking.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "message", ex.getMessage(),
                "timestamp", Instant.now().toString()
        ));
    }
    // Add handlers for NotFound, AccessDenied, MethodArgumentNotValid, etc.
}
