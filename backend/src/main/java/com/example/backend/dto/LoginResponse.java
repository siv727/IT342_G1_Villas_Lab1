package com.example.backend.dto;

public class LoginResponse {
    private final Long userId;
    private final String message;

    public LoginResponse(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public String getMessage() { return message; }
}
