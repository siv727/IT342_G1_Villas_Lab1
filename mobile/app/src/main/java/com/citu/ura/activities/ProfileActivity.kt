package com.citu.ura.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.citu.ura.R
import com.citu.ura.components.LogoutButton
import com.citu.ura.components.ProfileInfoRow
import com.citu.ura.network.ApiService
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
 * ProfileActivity – equivalent to ProfilePage.jsx in the web frontend.
 *
 * Displays the authenticated user's profile by calling GET /api/user/{id}.
 * Uses reusable components: LogoutButton, ProfileInfoRow.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var logoutButton: LogoutButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var profileContent: LinearLayout
    private lateinit var tvInitials: TextView
    private lateinit var rowFirstName: ProfileInfoRow
    private lateinit var rowLastName: ProfileInfoRow
    private lateinit var rowEmail: ProfileInfoRow
    private lateinit var btnEditProfile: MaterialButton

    private val apiService = ApiService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Check authentication (equivalent to if (!token || !userId) navigate("/login"))
        if (!TokenManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Bind views
        logoutButton = findViewById(R.id.logoutButton)
        progressBar = findViewById(R.id.progressBar)
        tvError = findViewById(R.id.tvError)
        profileContent = findViewById(R.id.profileContent)
        tvInitials = findViewById(R.id.tvInitials)
        rowFirstName = findViewById(R.id.rowFirstName)
        rowLastName = findViewById(R.id.rowLastName)
        rowEmail = findViewById(R.id.rowEmail)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        // Logout button handler (equivalent to LogoutButton component behavior)
        logoutButton.setOnLogoutClickListener {
            handleLogout()
        }

        // Edit Profile button
        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, ProfileUpdateActivity::class.java))
        }

        // Fetch profile data
        fetchProfile()
    }

    override fun onResume() {
        super.onResume()
        // Refresh profile data when coming back from update screen
        if (TokenManager.isLoggedIn()) {
            fetchProfile()
        }
    }

    /**
     * Fetch user profile from the backend – equivalent to the useEffect in ProfilePage.jsx
     */
    private fun fetchProfile() {
        val userId = TokenManager.getUserId()
        if (userId == -1L) {
            showError("User ID not found. Please login again.")
            return
        }

        progressBar.visibility = View.VISIBLE
        profileContent.visibility = View.GONE
        tvError.visibility = View.GONE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.getProfile(userId)
                }

                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        // Set avatar initials (equivalent to const initials = ... in ProfilePage.jsx)
                        val initials = ((user.firstname.firstOrNull()?.toString() ?: "") +
                                (user.lastname.firstOrNull()?.toString() ?: "")).uppercase()
                        tvInitials.text = initials.ifEmpty { "U" }

                        // Set profile info rows (reusable components)
                        rowFirstName.setData("First Name", user.firstname)
                        rowLastName.setData("Last Name", user.lastname)
                        rowEmail.setData("Email", user.email)

                        profileContent.visibility = View.VISIBLE
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            // Token expired – clear and redirect to login
                            TokenManager.clearAll()
                            navigateToLogin()
                        }
                        403 -> showError("Access denied. Please login again.")
                        404 -> showError("Profile not found. Please login again.")
                        in 500..599 -> showError("Server error. Please try again later.")
                        else -> {
                            val errorBody = response.errorBody()?.string()
                            showError(parseErrorBody(errorBody) ?: "Failed to load profile.")
                        }
                    }
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
                progressBar.visibility = View.GONE
            }
        }
    }

    /**
     * Handle logout – equivalent to logoutUser() + navigate("/login") in the web frontend
     */
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

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
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