package com.example.backend.util;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing and validating user input.
 * Prevents XSS, script injection, and enforces field-level rules.
 */
public final class InputSanitizer {

    private InputSanitizer() {}

    // Matches HTML/XML tags
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    // Name: letters, spaces, hyphens, apostrophes, periods (supports accented chars)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    // Basic email format
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Strips HTML tags and trims whitespace.
     */
    public static String stripHtml(String input) {
        if (input == null) return null;
        return HTML_TAG_PATTERN.matcher(input).replaceAll("").trim();
    }

    /**
     * Validates that a string is not null/blank after trimming.
     */
    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required and cannot be blank.");
        }
    }

    /**
     * Validates a name field: not blank, only letters/spaces/hyphens/apostrophes,
     * max 50 chars.
     */
    public static String sanitizeName(String value, String fieldName) {
        String sanitized = stripHtml(value);
        requireNotBlank(sanitized, fieldName);

        if (sanitized.length() > 50) {
            throw new IllegalArgumentException(fieldName + " must be 50 characters or fewer.");
        }

        if (!NAME_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException(
                fieldName + " can only contain letters, spaces, hyphens, apostrophes, and periods."
            );
        }

        return sanitized;
    }

    /**
     * Validates and sanitizes an email field.
     */
    public static String sanitizeEmail(String value) {
        String sanitized = stripHtml(value);
        requireNotBlank(sanitized, "Email");

        if (sanitized.length() > 100) {
            throw new IllegalArgumentException("Email must be 100 characters or fewer.");
        }

        if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }

        return sanitized.toLowerCase();
    }
}
