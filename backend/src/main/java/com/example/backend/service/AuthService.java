package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegistrationRequest;
import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository, JwtService jwtService,
                       RefreshTokenService refreshTokenService, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public JwtService getJwtService() {
        return jwtService;
    }

    public RefreshTokenService getRefreshTokenService() {
        return refreshTokenService;
    }

    public User registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        validatePassword(request.password());

        User user = new User();
        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    private void validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < 8) {
            errors.add("at least 8 characters");
        }
        if (!Pattern.compile("[A-Z]").matcher(password != null ? password : "").find()) {
            errors.add("one uppercase letter");
        }
        if (!Pattern.compile("[a-z]").matcher(password != null ? password : "").find()) {
            errors.add("one lowercase letter");
        }
        if (!Pattern.compile("[0-9]").matcher(password != null ? password : "").find()) {
            errors.add("one digit");
        }
        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password != null ? password : "").find()) {
            errors.add("one special character");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                "Password does not meet the required criteria. It must have "
                + String.join(", ", errors.subList(0, errors.size() - 1))
                + (errors.size() > 1 ? ", and " + errors.get(errors.size() - 1) : errors.get(0))
                + "."
            );
        }
    }

    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    public User verifyRefreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        return refreshToken.getUser();
    }

    public void logout(String accessToken) {
        // Blacklist the access token so it can't be reused
        Instant expiry = jwtService.extractExpiration(accessToken).toInstant();
        tokenBlacklistService.blacklistToken(accessToken, expiry);

        // Delete the user's refresh tokens
        String email = jwtService.extractEmail(accessToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        refreshTokenService.deleteByUser(user);
    }
}
