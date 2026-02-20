package com.citu.ura.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.citu.ura.R
import com.citu.ura.components.AppInputField
import com.citu.ura.components.LogoutButton
import com.citu.ura.model.UserResponse
import com.citu.ura.network.ApiService
import com.citu.ura.network.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ProfileUpdateActivity – equivalent to ProfileUpdatePage.jsx in the web frontend.
 *
 * Allows the user to update their profile by calling PUT /api/user/{id}.
 * Uses reusable components: AppInputField, LogoutButton.
 */
class ProfileUpdateActivity : AppCompatActivity() {

    private lateinit var logoutButton: LogoutButton
    private lateinit var progressBar: ProgressBar
    private lateinit var formContent: LinearLayout
    private lateinit var tvInitials: TextView
    private lateinit var tvError: TextView
    private lateinit var tvSuccess: TextView
    private lateinit var inputFirstname: AppInputField
    private lateinit var inputLastname: AppInputField
    private lateinit var inputEmail: AppInputField
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var progressBarSaving: ProgressBar

    private val apiService = ApiService.create()
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)

        // Check authentication
        if (!TokenManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        userId = TokenManager.getUserId()

        // Bind views
        logoutButton = findViewById(R.id.logoutButton)
        progressBar = findViewById(R.id.progressBar)
        formContent = findViewById(R.id.formContent)
        tvInitials = findViewById(R.id.tvInitials)
        tvError = findViewById(R.id.tvError)
        tvSuccess = findViewById(R.id.tvSuccess)
        inputFirstname = findViewById(R.id.inputFirstname)
        inputLastname = findViewById(R.id.inputLastname)
        inputEmail = findViewById(R.id.inputEmail)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        progressBarSaving = findViewById(R.id.progressBarSaving)

        // Logout button handler
        logoutButton.setOnLogoutClickListener {
            handleLogout()
        }

        // Save button
        btnSave.setOnClickListener { handleSave() }

        // Cancel button – go back to profile
        btnCancel.setOnClickListener { finish() }

        // Fetch current profile to pre-fill form (equivalent to useEffect in ProfileUpdatePage.jsx)
        fetchProfile()
    }

    /**
     * Fetch current profile and pre-fill form fields.
     * Equivalent to the useEffect -> fetchProfile in ProfileUpdatePage.jsx.
     */
    private fun fetchProfile() {
        if (userId == -1L) {
            showError("User ID not found. Please login again.")
            return
        }

        progressBar.visibility = View.VISIBLE
        formContent.visibility = View.GONE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getProfile(userId)
                }

                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Pre-fill form fields (equivalent to setFormData in ProfileUpdatePage.jsx)
                        inputFirstname.setText(user.firstname)
                        inputLastname.setText(user.lastname)
                        inputEmail.setText(user.email)

                        // Update avatar initials
                        updateInitials(user.firstname, user.lastname)

                        formContent.visibility = View.VISIBLE
                    }
                } else {
                    if (response.code() == 401) {
                        TokenManager.clearAll()
                        navigateToLogin()
                    } else {
                        showError("Failed to load profile.")
                    }
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    /**
     * Handle save – equivalent to handleSubmit in ProfileUpdatePage.jsx
     */
    private fun handleSave() {
        val firstname = inputFirstname.getText()
        val lastname = inputLastname.getText()
        val email = inputEmail.getText()

        // Validate (equivalent to validate() in ProfileUpdatePage.jsx)
        val validationError = validate(firstname, lastname, email)
        if (validationError != null) {
            showError(validationError)
            return
        }

        setSaving(true)
        hideError()
        hideSuccess()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.updateProfile(
                        userId,
                        UserResponse(userId, firstname, lastname, email, null)
                    )
                }

                if (response.isSuccessful) {
                    showSuccess("Profile updated successfully!")
                    // Update initials with new values
                    updateInitials(firstname, lastname)
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError(errorBody ?: "Failed to update profile.")
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                setSaving(false)
            }
        }
    }

    /**
     * Validate form fields – equivalent to validate() in ProfileUpdatePage.jsx
     */
    private fun validate(firstname: String, lastname: String, email: String): String? {
        if (firstname.isEmpty()) return "First Name is required."
        if (lastname.isEmpty()) return "Last Name is required."
        if (email.isEmpty()) return "Email is required."
        return null
    }

    private fun updateInitials(firstname: String, lastname: String) {
        val initials = ((firstname.firstOrNull()?.toString() ?: "") +
                (lastname.firstOrNull()?.toString() ?: "")).uppercase()
        tvInitials.text = initials.ifEmpty { "U" }
    }

    private fun handleLogout() {
        TokenManager.clearAll()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setSaving(isSaving: Boolean) {
        btnSave.isEnabled = !isSaving
        btnSave.text = if (isSaving) "Saving…" else "Save Changes"
        progressBarSaving.visibility = if (isSaving) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        tvSuccess.visibility = View.GONE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private fun showSuccess(message: String) {
        tvSuccess.text = message
        tvSuccess.visibility = View.VISIBLE
        tvError.visibility = View.GONE
    }

    private fun hideSuccess() {
        tvSuccess.visibility = View.GONE
    }
}