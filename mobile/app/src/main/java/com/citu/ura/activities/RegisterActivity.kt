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
import com.citu.ura.model.RegistrationRequest
import com.citu.ura.network.ApiService
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * RegisterActivity – equivalent to RegisterPage.jsx in the web frontend.
 *
 * Uses the reusable AuthForm pattern (AppInputField components) and calls
 * the backend's POST /api/auth/register endpoint via Retrofit.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var inputFirstname: AppInputField
    private lateinit var inputLastname: AppInputField
    private lateinit var inputEmail: AppInputField
    private lateinit var inputPassword: AppInputField
    private lateinit var inputConfirmPassword: AppInputField
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvError: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGoToLogin: TextView

    private val apiService = ApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Bind views
        inputFirstname = findViewById(R.id.inputFirstname)
        inputLastname = findViewById(R.id.inputLastname)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvError = findViewById(R.id.tvError)
        progressBar = findViewById(R.id.progressBar)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Create Account button click
        btnRegister.setOnClickListener { handleRegister() }

        // Navigate to Login
        tvGoToLogin.setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun handleRegister() {
        val firstname = inputFirstname.getText()
        val lastname = inputLastname.getText()
        val email = inputEmail.getText()
        val password = inputPassword.getText()
        val confirmPassword = inputConfirmPassword.getText()

        // Validate fields (same validation as AuthForm.jsx)
        val validationError = validate(firstname, lastname, email, password, confirmPassword)
        if (validationError != null) {
            showError(validationError)
            return
        }

        setLoading(true)
        hideError()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.register(
                        RegistrationRequest(firstname, lastname, email, password)
                    )
                }

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration successful! Please login.",
                        Toast.LENGTH_LONG
                    ).show()
                    // After successful registration, redirect to login (same as web)
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError(errorBody ?: "Registration failed. Please try again.")
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Validate form fields – equivalent to validate() in AuthForm.jsx
     */
    private fun validate(
        firstname: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (firstname.isEmpty()) return "First Name is required."
        if (lastname.isEmpty()) return "Last Name is required."
        if (email.isEmpty()) return "Email is required."
        if (password.isEmpty()) return "Password is required."
        if (confirmPassword.isEmpty()) return "Confirm Password is required."
        if (password != confirmPassword) return "Password and Confirm Password do not match."
        return null
    }

    private fun setLoading(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        btnRegister.text = if (isLoading) "Please wait…" else "Create Account"
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }
}