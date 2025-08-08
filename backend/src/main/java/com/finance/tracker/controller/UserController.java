package com.finance.tracker.controller;

import com.finance.tracker.api.UserApi;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.ModelApiResponse;
import com.finance.tracker.model.UserProfile;
import com.finance.tracker.service.UserService;
import com.finance.tracker.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user-related operations
 * Implements the generated UserApi interface
 */
@RestController
public class UserController implements UserApi {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<ModelApiResponse> getUserProfile() {
        logger.info("Retrieving user profile");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthorized access attempt to user profile");
            return ResponseBuilder.error(401, "Unauthorized", "Authentication required");
        }
        
        UserEntity user = (UserEntity) authentication.getPrincipal();
        
        try {
            // Delegate to service layer for business logic and model conversion
            UserProfile userProfile = userService.getUserProfile(user.getUsername());
            
            logger.info("User profile retrieved successfully for user: {}", user.getUsername());
            return ResponseBuilder.success(userProfile, "User profile retrieved successfully");
            
        } catch (Exception e) {
            logger.error("Failed to retrieve user profile for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseBuilder.error(500, "Internal Server Error", "Failed to retrieve user profile");
        }
    }
}
