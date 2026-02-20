package com.citu.ura.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.citu.ura.R
import com.citu.ura.components.AppInputField
import com.citu.ura.model.LoginRequest
import com.citu.ura.network.ApiService
import com.citu.ura.network.AuthUtils
import com.citu.ura.network.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException

/**
 * LoginActivity – equivalent to LoginPage.jsx in the web frontend.
 *
 * Uses the reusable AuthForm pattern (AppInputField components) and calls
 * the backend's POST /api/auth/login endpoint via Retrofit.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail: AppInputField
    private lateinit var inputPassword: AppInputField
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGoToRegister: TextView

    private val apiService = ApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, go directly to profile
        if (TokenManager.isLoggedIn()) {
            navigateToProfile()
            return
        }

        setContentView(R.layout.activity_login)

        // Bind views
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        // Sign In button click
        btnLogin.setOnClickListener { handleLogin() }

        // Navigate to Register
        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        // Validate fields
        val email = inputEmail.getText()
        val password = inputPassword.getText()

        if (email.isEmpty()) {
            showError("Email is required.")
            return
        }
        if (password.isEmpty()) {
            showError("Password is required.")
            return
        }

        // Show loading state
        setLoading(true)
        hideError()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.login(LoginRequest(email, password))
                }

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token

                    if (token != null) {
                        // Save token (equivalent to localStorage.setItem("token", token))
                        TokenManager.saveToken(token)

                        // Decode userId from JWT (equivalent to getUserIdFromToken in auth.js)
                        val userId = AuthUtils.getUserIdFromToken(token)
                        if (userId != null) {
                            TokenManager.saveUserId(userId)
                        }

                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        navigateToProfile()
                    } else {
                        showError("Invalid response from server.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = when (response.code()) {
                        400 -> parseErrorBody(errorBody) ?: "Invalid request. Please check your input."
                        401 -> "Invalid email or password. Please try again."
                        403 -> "Access denied. Your account may be disabled."
                        404 -> "Service not found. Please try again later."
                        409 -> parseErrorBody(errorBody) ?: "This account already exists."
                        in 500..599 -> "Server error. Please try again later."
                        else -> parseErrorBody(errorBody) ?: "Login failed. Please try again."
                    }
                    showError(message)
                }
            } catch (e: SocketTimeoutException) {
                showError("Connection timed out. Please check your internet and try again.")
            } catch (e: UnknownHostException) {
                showError("Unable to reach the server. Please check your internet connection.")
            } catch (e: ConnectException) {
                showError("Unable to connect to the server. Please try again later.")
            } catch (e: IOException) {
                showError("A network error occurred. Please check your connection and try again.")
            } catch (e: Exception) {
                showError("An unexpected error occurred. Please try again.")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Please wait…" else "Sign In"
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    /**
     * Parse the error body from the server response.
     * Tries to extract a "message" or "error" field from JSON, falls back to plain text.
     */
    private fun parseErrorBody(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", null)
                ?: json.optString("error", null)
                ?: errorBody.take(200)
        } catch (e: Exception) {
            // Not JSON – return plain text (trimmed)
            errorBody.take(200)
        }
    }
}