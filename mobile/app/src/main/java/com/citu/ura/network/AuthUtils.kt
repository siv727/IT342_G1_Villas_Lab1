package com.citu.ura.network

import android.util.Base64
import org.json.JSONObject

/**
 * AuthUtils – JWT decoding utilities, equivalent to lib/auth.js in the web frontend.
 *
 * Decodes the JWT token payload to extract the userId without any external library.
 */
object AuthUtils {

    /**
     * Decode a JWT token payload without a library.
     * Returns the parsed JSONObject payload, or null on failure.
     */
    fun decodeToken(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
            JSONObject(payload)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract the user ID from a JWT token.
     * Checks numeric claims first (id, userId), falls back to sub.
     */
    fun getUserIdFromToken(token: String): Long? {
        val payload = decodeToken(token) ?: return null
        return when {
            payload.has("userId") -> payload.getLong("userId")
            payload.has("id") -> payload.getLong("id")
            payload.has("sub") -> payload.optLong("sub", -1L).takeIf { it != -1L }
            else -> null
        }
    }
}