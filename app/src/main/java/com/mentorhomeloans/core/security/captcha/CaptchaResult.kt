package com.mentorhomeloans.core.security.captcha

/**
 * Result wrapper for CAPTCHA operations.
 */
sealed interface CaptchaResult {
    /**
     * Successfully generated a non-empty, fresh token.
     *
     * @param token The single-use verification token.
     * @param action The action associated with this token (e.g., "LOGIN").
     */
    data class Success(val token: String, val action: String) : CaptchaResult

    /**
     * CAPTCHA token generation or initialization failed.
     *
     * @param error The domain-level error categorization.
     * @param cause The underlying exception, if available.
     */
    data class Failure(val error: CaptchaError, val cause: Throwable? = null) : CaptchaResult
}

/**
 * Domain-specific CAPTCHA error types.
 */
sealed class CaptchaError(val message: String) {
    object InvalidSiteKey : CaptchaError("Invalid or missing CAPTCHA site key.")
    object InvalidPackageName : CaptchaError("Invalid or mismatching application package name.")
    object NetworkError : CaptchaError("Network error during security check. Please check your internet connection.")
    object InitializationFailed : CaptchaError("Security check (reCAPTCHA) initialization failed.")
    object EmptyToken : CaptchaError("Security check returned an empty verification token.")
    data class GenerationFailed(val details: String) : CaptchaError("Security verification failed: $details")
}
