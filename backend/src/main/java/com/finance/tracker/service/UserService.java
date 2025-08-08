package com.finance.tracker.service;

import com.finance.tracker.model.UserProfile;

/**
 * Service interface for user-related operations
 */
public interface UserService {
    
    /**
     * Get user profile for the authenticated user
     * @param username the username of the authenticated user
     * @return UserProfile with user details
     */
    UserProfile getUserProfile(String username);
}
