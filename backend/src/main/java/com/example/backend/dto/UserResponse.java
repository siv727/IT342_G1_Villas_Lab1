package com.example.backend.dto;

public record UserResponse(
    Long id,
    String firstname,
    String lastname,
    String email,
    String profilePicture
) {
}
