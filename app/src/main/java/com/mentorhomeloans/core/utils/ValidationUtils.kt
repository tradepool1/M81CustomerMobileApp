package com.mentorhomeloans.core.utils

import android.util.Patterns

/**
 * Common validation utilities.
 */
object ValidationUtils {

    /**
     * Validates an Indian mobile number.
     */
    fun validateMobileNumber(mobile: String): String? {
        if (mobile.isBlank()) return "Mobile number cannot be empty"
        if (mobile.length != 10 || !mobile.all { it.isDigit() }) return "Please enter a valid 10-digit number"
        if (!mobile.startsWith("6") && !mobile.startsWith("7") && !mobile.startsWith("8") && !mobile.startsWith("9")) {
            return "Mobile number must start with 6, 7, 8, or 9"
        }
        return null
    }

    /**
     * Validates email structures.
     */
    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email address cannot be empty"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Please enter a valid email address"
        return null
    }
}
