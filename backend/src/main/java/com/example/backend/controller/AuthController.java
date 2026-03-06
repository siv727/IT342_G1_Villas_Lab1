package com.example.backend.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RegistrationRequest;
import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AuthService;
import com.example.backend.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest req, HttpServletResponse response) {
        try {
            User user = authService.registerUser(req);
            setTokenCookies(response, user);
            return ResponseEntity.ok(new LoginResponse(user.getId(), "Registration successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        try {
            User user = authService.authenticate(req);
            setTokenCookies(response, user);
            return ResponseEntity.ok(new LoginResponse(user.getId(), "Login successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshTokenStr = extractCookie(request, "refreshToken");
            if (refreshTokenStr == null) {
                return ResponseEntity.badRequest().body("Refresh token not found");
            }

            User user = authService.verifyRefreshToken(refreshTokenStr);

            // Generate new access token and set it as a cookie
            String newAccessToken = authService.getJwtService().generateToken(user.getEmail(), user.getId());
            Cookie accessCookie = createCookie("accessToken", newAccessToken, (int) (accessTokenExpiration / 1000), "/");
            response.addCookie(accessCookie);

            return ResponseEntity.ok(new LoginResponse(user.getId(), "Token refreshed"));
        } catch (IllegalArgumentException e) {
            // Clear cookies on invalid refresh
            clearTokenCookies(response);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = extractCookie(request, "accessToken");
            if (accessToken != null) {
                authService.logout(accessToken);
            }
        } catch (Exception e) {
            // Even if blacklisting fails, still clear cookies
        }
        clearTokenCookies(response);
        return ResponseEntity.ok("Logged out successfully");
    }

    // --- Cookie helper methods ---

    private void setTokenCookies(HttpServletResponse response, User user) {
        String accessToken = authService.getJwtService().generateToken(user.getEmail(), user.getId());
        RefreshToken refreshToken = authService.getRefreshTokenService().createRefreshToken(user);

        Cookie accessCookie = createCookie("accessToken", accessToken, (int) (accessTokenExpiration / 1000), "/");
        Cookie refreshCookie = createCookie("refreshToken", refreshToken.getToken(), (int) (refreshTokenExpiration / 1000), "/api/auth");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private void clearTokenCookies(HttpServletResponse response) {
        Cookie accessCookie = createCookie("accessToken", "", 0, "/");
        Cookie refreshCookie = createCookie("refreshToken", "", 0, "/api/auth");
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    private Cookie createCookie(String name, String value, int maxAgeSeconds, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath(path);
        cookie.setMaxAge(maxAgeSeconds);
        return cookie;
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
