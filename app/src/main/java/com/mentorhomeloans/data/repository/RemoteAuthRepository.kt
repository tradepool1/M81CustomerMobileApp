package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.data.remote.api.AuthApiService
import com.mentorhomeloans.data.remote.dto.GenerateOtpRequestDto
import com.mentorhomeloans.data.remote.dto.OtpVerificationRequestDto
import com.mentorhomeloans.data.remote.dto.PageContentDto
import com.mentorhomeloans.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real network-backed implementation of [AuthRepository].
 *
 * Calls the live MobileApp API endpoints:
 *  - POST GenerateOTP       → sends OTP to the given phone number
 *  - POST OTP_Verification  → verifies OTP and retrieves JWT token
 *
 * On successful verification the JWT token is stored securely via [SessionManager].
 */
@Singleton
class RemoteAuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
    private val preferencesDataStore: UserPreferencesDataStore
) : AuthRepository {

    /**
     * Calls GenerateOTP endpoint to send OTP to [mobileNumber].
     *
     * Returns [Result.Success] with the phone number (used as session key,
     * since this API has no separate sessionId) when Status == true.
     * Returns [Result.Error] if the API reports failure or a network error occurs.
     */
    override suspend fun sendOtp(mobileNumber: String): Result<String> {
        return try {
            val response = authApiService.generateOtp(
                GenerateOtpRequestDto(phoneNo = mobileNumber)
            )
            if (response.status) {
                Result.Success(mobileNumber)
            } else {
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Calls OTP_Verification endpoint to verify [otp] for [mobileNumber].
     *
     * On success, saves the returned tokens to [SessionManager] via [saveSession].
     * The [sessionId] parameter is the phone number (returned from [sendOtp])
     * and is used as the phoneNo in the verification request.
     *
     * Returns [Result.Success] with true on verified, [Result.Error] otherwise.
     */
    override suspend fun verifyOtp(
        mobileNumber: String,
        otp: String,
        sessionId: String
    ): Result<Boolean> {
        return try {
            val response = authApiService.verifyOtp(
                OtpVerificationRequestDto(
                    phoneNo = mobileNumber,
                    otpCode = otp
                )
            )
            if (response.status) {
                val accessToken = response.accessToken ?: "jwt_session_$mobileNumber"
                val refreshToken = response.refreshToken ?: ""

                sessionManager.saveSession(
                    jwtToken      = accessToken,
                    refreshToken  = refreshToken,
                    mobileNumber  = mobileNumber,
                    jwtExpiry     = response.accessTokenExpiresAt,
                    refreshExpiry = response.refreshTokenExpiresAt
                )
                preferencesDataStore.setLoggedIn(true)
                preferencesDataStore.setOnboardingCompleted(true)
                Result.Success(true)
            } else {
                Result.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Clears the locally stored session (JWT token, mobile number).
     * Note: This API does not have a logout endpoint.
     */
    override suspend fun logout(): Result<Unit> {
        sessionManager.clearSession()
        preferencesDataStore.setLoggedIn(false)
        return Result.Success(Unit)
    }

    /**
     * Emits whether a valid session (JWT token) currently exists.
     */
    override fun isSessionActive(): Flow<Boolean> {
        return sessionManager.isSessionActive
    }

    /**
     * Fetches dynamic CMS page content (HTML) for the given [pageKey].
     */
    override suspend fun getPageContent(pageKey: String): Result<PageContentDto> {
        return try {
            val responseList = authApiService.getPageContent(pageKey)
            val page = responseList.firstOrNull()
            if (page != null) {
                Result.Success(page)
            } else {
                Result.Error(Exception("No content found for $pageKey"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
