package com.citu.ura.model

data class RegistrationRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)