package com.finance.tracker.config;

import com.finance.tracker.model.ModelApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

/**
 * Global exception handler for all controllers
 * Uses the standardized ModelApiResponse format for all error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ModelApiResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        ModelApiResponse errorResponse = new ModelApiResponse()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Authentication failed: " + ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .body("")
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ModelApiResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        ModelApiResponse errorResponse = new ModelApiResponse()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Access denied: " + ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .body("")
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ModelApiResponse> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        ModelApiResponse errorResponse = new ModelApiResponse()
                .status(ex.getStatusCode().value())
                .error(ex.getStatusCode().toString())
                .message(ex.getReason() != null ? ex.getReason() : "An error occurred")
                .path(request.getDescription(false).replace("uri=", ""))
                .body("")
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ModelApiResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        ModelApiResponse errorResponse = new ModelApiResponse()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .body("")
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ModelApiResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = "Validation failed";
        if (ex.getBindingResult().hasFieldErrors()) {
            message = ex.getBindingResult().getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Validation failed");
        }
        
        ModelApiResponse errorResponse = new ModelApiResponse()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .body("")
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ModelApiResponse> handleGenericException(
            Exception ex, WebRequest request) {
        ModelApiResponse errorResponse = new ModelApiResponse()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .body("")
                .timestamp(OffsetDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
