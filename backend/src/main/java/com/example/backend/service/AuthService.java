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

        User user = new User();
        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
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
