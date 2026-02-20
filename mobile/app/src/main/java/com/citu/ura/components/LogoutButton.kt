package com.citu.ura.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.citu.ura.R
import com.google.android.material.button.MaterialButton

/**
 * LogoutButton – reusable logout button component.
 * Equivalent to LogoutButton.jsx in the web frontend.
 *
 * Usage in XML:
 *   <com.example.mobile.components.LogoutButton
 *       android:id="@+id/logoutButton"
 *       android:layout_width="wrap_content"
 *       android:layout_height="wrap_content" />
 *
 * In Activity/Fragment:
 *   logoutButton.setOnLogoutClickListener { /* handle logout */ }
 */
class LogoutButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val button: MaterialButton

    init {
        LayoutInflater.from(context).inflate(R.layout.component_logout_button, this, true)
        button = findViewById(R.id.btnLogout)
    }

    fun setOnLogoutClickListener(listener: () -> Unit) {
        button.setOnClickListener { listener() }
    }
}