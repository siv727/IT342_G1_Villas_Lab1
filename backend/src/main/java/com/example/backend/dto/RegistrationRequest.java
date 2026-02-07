package com.example.backend.dto;


public record RegistrationRequest(
    String firstname,
    String lastname,
    String email,
    String password
) {
}
