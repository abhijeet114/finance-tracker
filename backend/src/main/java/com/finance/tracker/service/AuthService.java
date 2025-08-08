package com.finance.tracker.service;

import com.finance.tracker.dto.auth.AuthResponse;
import com.finance.tracker.dto.auth.LoginRequest;
import com.finance.tracker.dto.auth.RegisterRequest;
import com.finance.tracker.entity.Role;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service for user registration and login
 * Handles all business logic and model conversions
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user
     * @param loginRequest the login request from OpenAPI model
     * @return AuthResponse for the OpenAPI model
     */
    public com.finance.tracker.model.AuthResponse login(com.finance.tracker.model.LoginRequest loginRequest) {
        // Convert from OpenAPI model to DTO
        LoginRequest dto = LoginRequest.builder()
                .username(loginRequest.getUsername())
                .password(loginRequest.getPassword())
                .build();
        
        // Process login
        AuthResponse response = performLogin(dto);
        
        // Convert back to OpenAPI model
        return new com.finance.tracker.model.AuthResponse()
                .token(response.getToken())
                .type(response.getType())
                .username(response.getUsername())
                .email(response.getEmail());
    }

    /**
     * Register a new user
     * @param registerRequest the registration request from OpenAPI model
     * @return AuthResponse for the OpenAPI model
     */
    public com.finance.tracker.model.AuthResponse register(com.finance.tracker.model.RegisterRequest registerRequest) {
        // Convert from OpenAPI model to DTO
        RegisterRequest dto = RegisterRequest.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();
        
        // Process registration
        AuthResponse response = performRegistration(dto);
        
        // Convert back to OpenAPI model
        return new com.finance.tracker.model.AuthResponse()
                .token(response.getToken())
                .type(response.getType())
                .username(response.getUsername())
                .email(response.getEmail());
    }

    private AuthResponse performRegistration(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private AuthResponse performLogin(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserEntity user = (UserEntity) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
