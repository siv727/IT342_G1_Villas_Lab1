package com.citu.ura

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.citu.ura.activities.LoginActivity
import com.citu.ura.activities.ProfileActivity
import com.citu.ura.network.TokenManager

/**
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