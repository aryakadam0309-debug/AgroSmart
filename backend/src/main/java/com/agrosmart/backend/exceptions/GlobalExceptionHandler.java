package com.agrosmart.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpired(TokenExpiredException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "ACCESS_TOKEN_EXPIRED");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(TokenTamperedException.class)
    public ResponseEntity<Map<String, Object>> handleTokenTampered(TokenTamperedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INVALID_ACCESS_TOKEN");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "UNAUTHORIZED");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<Map<String, Object>> handleBookingConflict(BookingConflictException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "BOOKING_CONFLICT");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getStatusCode().toString());
        body.put("message", ex.getReason());
        body.put("status", ex.getStatusCode().value());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
