package com.finance.tracker.util;

import com.finance.tracker.model.ModelApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

/**
 * Utility class for building standardized API responses
 */
public class ResponseBuilder {
    
    /**
     * Get current request path
     */
    private static String getCurrentPath() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        return request.getRequestURI();
    }
    
    /**
     * Create a successful response with data
     */
    public static ResponseEntity<ModelApiResponse> success(Object data, String message) {
        ModelApiResponse response = new ModelApiResponse()
                .status(200)
                .error("")
                .message(message)
                .path(getCurrentPath())
                .timestamp(OffsetDateTime.now())
                .body(data);  // Use the builder method instead of setBody
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create a successful response with custom status
     */
    public static ResponseEntity<ModelApiResponse> success(int status, Object data, String message) {
        ModelApiResponse response = new ModelApiResponse()
                .status(status)
                .error("")
                .message(message)
                .path(getCurrentPath())
                .timestamp(OffsetDateTime.now())
                .body(data);  // Use the builder method instead of setBody
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Create an error response
     */
    public static ResponseEntity<ModelApiResponse> error(int status, String error, String message) {
        ModelApiResponse response = new ModelApiResponse()
                .status(status)
                .error(error)
                .message(message)
                .path(getCurrentPath())
                .timestamp(OffsetDateTime.now())
                .body(null);  // Use the builder method for consistency
        
        return ResponseEntity.status(status).body(response);
    }
}
