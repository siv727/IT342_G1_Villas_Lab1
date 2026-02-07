package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    public Long id;
    public String firstname;
    public String lastname;
    public String email;
    public String password;
    public String profilePicture;
}
