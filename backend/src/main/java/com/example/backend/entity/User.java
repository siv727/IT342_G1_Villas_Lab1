package com.example.backend.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class User {
    public Long id;
    public String firstname;
    public String lastname;
    public String email;
    public String password;
    public String profilePicture;
}
