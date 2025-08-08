package com.finance.tracker.controller;

import com.finance.tracker.api.AuthenticationApi;
import com.finance.tracker.model.ModelApiResponse;
import com.finance.tracker.service.AuthService;
import com.finance.tracker.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for user registration and login
 * Implements the generated AuthenticationApi interface
 */
@RestController
public class AuthController implements AuthenticationApi {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<ModelApiResponse> loginUser(
            com.finance.tracker.model.LoginRequest loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.getUsername());
        try {
            // Delegate to service layer for all business logic and model conversion
            com.finance.tracker.model.AuthResponse authData = authService.login(loginRequest);
            
            logger.info("Login successful for username: {}", loginRequest.getUsername());
            return ResponseBuilder.success(authData, "Login successful");
            
        } catch (Exception e) {
            logger.warn("Login failed for username {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseBuilder.error(400, "Bad Request", "Invalid username or password");
        }
    }

    @Override
    public ResponseEntity<ModelApiResponse> registerUser(
            com.finance.tracker.model.RegisterRequest registerRequest) {
        logger.info("Registration attempt for username: {}", registerRequest.getUsername());
        try {
            // Delegate to service layer for all business logic and model conversion
            com.finance.tracker.model.AuthResponse authData = authService.register(registerRequest);
            
            logger.info("Registration successful for username: {}", registerRequest.getUsername());
            return ResponseBuilder.success(201, authData, "Registration successful");
            
        } catch (Exception e) {
            logger.warn("Registration failed for username {}: {}", registerRequest.getUsername(), e.getMessage());
            return ResponseBuilder.error(400, "Bad Request", "Registration failed: " + e.getMessage());
        }
    }
}
