package com.example.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.activities.LoginActivity
import com.example.mobile.activities.ProfileActivity
import com.example.mobile.network.TokenManager

/**
 * MainActivity – entry point of the app.
 * Initializes TokenManager and routes to Login or Profile based on auth state.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize TokenManager with application context
        TokenManager.init(applicationContext)

        // Route based on auth state (equivalent to React Router's protected routes)
        val intent = if (TokenManager.isLoggedIn()) {
            Intent(this, ProfileActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}