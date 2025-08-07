package com.finance.tracker.controller;

import com.finance.tracker.api.UserApi;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.UserProfile;
import org.springframework.http.HttpStatus;
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

    @Override
    public ResponseEntity<UserProfile> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            // This should actually be handled by the security filter now
            // but keeping as fallback
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UserEntity user = (UserEntity) authentication.getPrincipal();
        
        UserProfile userProfile = new UserProfile()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(UserProfile.RoleEnum.fromValue(user.getRole().name()))
                .createdAt(user.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
        
        return ResponseEntity.ok(userProfile);
    }
}
