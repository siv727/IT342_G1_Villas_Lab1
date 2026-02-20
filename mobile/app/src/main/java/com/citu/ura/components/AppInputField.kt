package com.citu.ura.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.citu.ura.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * AppInputField – reusable text input component with Material Design styling.
 * Equivalent to the <Input> + <Label> pattern in the web frontend's AuthForm.
 *
 * Custom XML attributes:
 *   app:fieldHint      – hint text for the input
 *   app:fieldInputType – text | email | password
 */
class AppInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val inputLayout: TextInputLayout
    private val editText: TextInputEditText

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.component_input_field, this, true)

        inputLayout = findViewById(R.id.tilInput)
        editText = findViewById(R.id.etInput)

        // Read custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AppInputField)
            val hint = typedArray.getString(R.styleable.AppInputField_fieldHint)
            val inputTypeEnum = typedArray.getInt(R.styleable.AppInputField_fieldInputType, 0)

            inputLayout.hint = hint

            editText.inputType = when (inputTypeEnum) {
                1 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                2 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                else -> InputType.TYPE_CLASS_TEXT
            }

            // Enable password toggle for password fields
            if (inputTypeEnum == 2) {
                inputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }

            typedArray.recycle()
        }
    }

    fun getText(): String = editText.text.toString().trim()

    fun setText(value: String) {
        editText.setText(value)
    }

    fun setError(message: String?) {
        inputLayout.error = message
        inputLayout.isErrorEnabled = message != null
    }

    fun clearError() {
        inputLayout.error = null
        inputLayout.isErrorEnabled = false
    }

    fun clear() {
        editText.text?.clear()
        clearError()
    }

    fun setHint(hint: String) {
        inputLayout.hint = hint
    }
}