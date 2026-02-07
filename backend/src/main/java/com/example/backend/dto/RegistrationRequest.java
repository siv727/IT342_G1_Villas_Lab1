package com.example.backend.dto;

public class RegistrationRequest {
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String password;

    public RegistrationRequest(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public String firstname() { return firstname; }
    public String lastname() { return lastname; }
    public String email() { return email; }
    public String password() { return password; }
}
