package com.finance.tracker.service;

import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.model.UserProfile;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation for user-related operations
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserProfile getUserProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfile()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(UserProfile.RoleEnum.fromValue(user.getRole().name()))
                .createdAt(user.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
    }
}
