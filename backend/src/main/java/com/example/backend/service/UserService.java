package com.example.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegistrationRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new UserResponse(
            user.getId(),
            user.getFirstname(),
            user.getLastname(),
            user.getEmail(),
            user.getProfilePicture()
        );
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

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
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
