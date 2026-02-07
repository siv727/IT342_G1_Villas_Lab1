package com.example.backend.dto;

public class UserResponse {
    private final Long id;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String profilePicture;

    public UserResponse(Long id, String firstname, String lastname, String email, String profilePicture) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public Long getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public String getProfilePicture() { return profilePicture; }
}
