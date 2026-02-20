package com.citu.ura.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.citu.ura.R

/**
 * ProfileInfoRow – reusable profile info display row.
 * Shows a label and value, similar to the profile info rows in ProfilePage.jsx.
 *
 * Usage in XML:
 *   <com.example.mobile.components.ProfileInfoRow
 *       android:id="@+id/rowEmail"
 *       android:layout_width="match_parent"
 *       android:layout_height="wrap_content" />
 *
 * In Activity:
 *   rowEmail.setData("Email", "user@example.com")
 */
class ProfileInfoRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val tvLabel: TextView
    private val tvValue: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.component_profile_info_row, this, true)
        tvLabel = findViewById(R.id.tvLabel)
        tvValue = findViewById(R.id.tvValue)
    }

    fun setData(label: String, value: String) {
        tvLabel.text = label
        tvValue.text = value
    }

    fun getValue(): String = tvValue.text.toString()
}