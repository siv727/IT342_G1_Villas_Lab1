package com.citu.ura.network

import com.citu.ura.model.LoginRequest
import com.citu.ura.model.LoginResponse
import com.citu.ura.model.RegistrationRequest
import com.citu.ura.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * ApiService – defines all API endpoints matching the Spring Boot backend controllers.
 *
 * Equivalent to authApi.js + profileApi.js in the web frontend.
 *
 * Endpoints:
 *   POST /api/auth/register  → AuthController.register()
 *   POST /api/auth/login     → AuthController.login()
 *   POST /api/auth/logout    → AuthController.logout()
 *   GET  /api/user/{id}      → UserController.getProfile()
 *   PUT  /api/user/{id}      → UserController.updateProfile()
 */
interface ApiService {

    // ── Auth Endpoints (AuthController) ──────────────────────────────────

    @POST("api/auth/register")
    suspend fun register(@Body request: RegistrationRequest): Response<LoginResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Void>

    // ── User/Profile Endpoints (UserController) ──────────────────────────

    @GET("api/user/{id}")
    suspend fun getProfile(@Path("id") id: Long): Response<UserResponse>

    @PUT("api/user/{id}")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body request: UserResponse
    ): Response<UserResponse>

    companion object {
        fun create(): ApiService {
            return RetrofitClient.instance.create(ApiService::class.java)
        }
    }
}