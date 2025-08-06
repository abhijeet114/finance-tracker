package com.finance.tracker.controller;

import com.finance.tracker.api.AuthenticationApi;
import com.finance.tracker.dto.auth.AuthResponse;
import com.finance.tracker.dto.auth.LoginRequest;
import com.finance.tracker.dto.auth.RegisterRequest;
import com.finance.tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for user registration and login
 * Implements the generated AuthenticationApi interface
 */
@RestController
@RequestMapping("/api")
public class AuthController implements AuthenticationApi {

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<com.finance.tracker.model.AuthResponse> loginUser(
            com.finance.tracker.model.LoginRequest loginRequest) {
        try {
            // Convert from generated model to DTO
            LoginRequest dto = LoginRequest.builder()
                    .username(loginRequest.getUsername())
                    .password(loginRequest.getPassword())
                    .build();
            
            AuthResponse response = authService.login(dto);
            
            // Convert response back to generated model
            com.finance.tracker.model.AuthResponse modelResponse = 
                    new com.finance.tracker.model.AuthResponse()
                            .token(response.getToken())
                            .type(response.getType())
                            .username(response.getUsername())
                            .email(response.getEmail());
            
            return ResponseEntity.ok(modelResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<com.finance.tracker.model.AuthResponse> registerUser(
            com.finance.tracker.model.RegisterRequest registerRequest) {
        try {
            // Convert from generated model to DTO
            RegisterRequest dto = RegisterRequest.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(registerRequest.getPassword())
                    .build();
            
            AuthResponse response = authService.register(dto);
            
            // Convert response back to generated model
            com.finance.tracker.model.AuthResponse modelResponse = 
                    new com.finance.tracker.model.AuthResponse()
                            .token(response.getToken())
                            .type(response.getType())
                            .username(response.getUsername())
                            .email(response.getEmail());
            
            return ResponseEntity.ok(modelResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
