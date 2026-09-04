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
     * Sends an OTP to the given mobile number for verification.
     *
     * @param mobileNumber The 10-digit mobile number to send OTP to.
     * @return [Result.Success] with a session reference ID on success,
     *         [Result.Error] on failure (invalid number, network error, etc.).
     */
    suspend fun sendOtp(mobileNumber: String): Result<String>

    /**
     * Verifies the OTP entered by the user against the backend session.
     *
     * On success, the JWT and refresh tokens are stored in [SessionManager].
     *
     * @param mobileNumber  The mobile number used to request the OTP.
     * @param otp           The 6-digit OTP entered by the user.
     * @param sessionId     The session reference ID returned by [sendOtp].
     * @return [Result.Success] with a Boolean (true = verified) on success,
     *         [Result.Error] on invalid OTP or network error.
     */
    suspend fun verifyOtp(mobileNumber: String, otp: String, sessionId: String): Result<Boolean>

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
}
