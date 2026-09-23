package com.mentorhomeloans.core.security.captcha

/**
 * Interface defining contract for CAPTCHA token generation and lifecycle management.
 */
interface CaptchaManager {

    /**
     * The verified application package name associated with CAPTCHA requests.
     */
    val currentPackageName: String

    /**
     * Initializes the underlying CAPTCHA SDK client.
     *
     * @return true if initialization succeeded or client is ready, false otherwise.
     */
    suspend fun initialize(): Boolean

    /**
     * Generates a fresh, non-expired CAPTCHA verification token.
     *
     * @param action The explicit action identifier (defaults to "LOGIN").
     * @return [CaptchaResult.Success] containing the token and action, or [CaptchaResult.Failure].
     */
    suspend fun generateToken(action: String = ACTION_LOGIN): CaptchaResult

    companion object {
        const val ACTION_LOGIN = "LOGIN"
    }
}
