package com.citu.ura.model

data class UserResponse(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val email: String,
    val profilePicture: String?
)