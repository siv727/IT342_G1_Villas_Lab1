package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.InputSanitizer;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserResponse(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getProfilePicture()
        );
    }

    public UserResponse updateProfile(Long userId, UserResponse request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Sanitize and validate inputs
        String firstname = InputSanitizer.sanitizeName(request.getFirstname(), "First name");
        String lastname = InputSanitizer.sanitizeName(request.getLastname(), "Last name");
        String email = InputSanitizer.sanitizeEmail(request.getEmail());

        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setProfilePicture(request.getProfilePicture());
        userRepository.save(user);

        return new UserResponse(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getProfilePicture()
        );
    }
}
