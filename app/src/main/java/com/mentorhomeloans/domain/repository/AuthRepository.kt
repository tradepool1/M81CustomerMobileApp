package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.dto.PageContentDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 *
 * Defines the contract between the domain layer and the data layer
 * for all login, OTP, and session management operations.
 * Concrete implementations live in the data layer (Mock or Remote).
 */
interface AuthRepository {

    /**
     * Authenticates a user using Customer ID, Password, and a CAPTCHA token.
     *
     * @param customerId   The unique customer identifier.
     * @param password     The user's account password.
     * @param captchaToken The verification token obtained from reCAPTCHA.
     * @return [Result.Success] with true on success, [Result.Error] on failure.
     */
    suspend fun login(customerId: String, password: String, captchaToken: String): Result<Boolean>

    /**
     * Logs out the current user by clearing all session tokens and cached data.
     *
     * @return [Result.Success] with Unit on success, [Result.Error] on failure.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Emits the current session validity as a [Flow].
     * True = valid active session, False = logged out or expired.
     *
     * @return A [Flow] that emits authentication state changes.
     */
    fun isSessionActive(): Flow<Boolean>

    /**
     * Fetches static page content from the backend by slug/key.
     *
     * @param pageKey The unique identifier for the page (e.g., 'aboutus', 'contactus').
     * @return [Result.Success] containing the page data, or [Result.Error].
     */
    suspend fun getPageContent(pageKey: String): Result<PageContentDto>

    /**
     * Updates the user's password.
     *
     * @param customerId   The ID of the customer.
     * @param oldPassword  The current password.
     * @param newPassword  The new password.
     * @param captchaToken The token from captcha verification.
     * @return [Result.Success] with the server message on success.
     */
    suspend fun updatePassword(
        customerId: String,
        oldPassword: String,
        newPassword: String,
        captchaToken: String
    ): Result<String>
}
