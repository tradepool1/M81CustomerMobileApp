package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.data.remote.api.AuthApiService
import com.mentorhomeloans.data.remote.dto.LoginRequestDto
import com.mentorhomeloans.data.remote.dto.PageContentDto
import com.mentorhomeloans.data.remote.dto.UpdatePasswordRequestDto
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
     * Calls Login endpoint with credentials and CAPTCHA.
     */
    override suspend fun login(
        customerId: String,
        password: String,
        captchaToken: String
    ): Result<Boolean> {
        return try {
            val response = authApiService.login(
                LoginRequestDto(
                    customerId = customerId,
                    password = password,
                    captchaToken = captchaToken
                )
            )
            if (response.status) {
                val accessToken = response.accessToken ?: "jwt_session_$customerId"
                val refreshToken = response.refreshToken ?: ""

                sessionManager.saveSession(
                    jwtToken      = accessToken,
                    refreshToken  = refreshToken,
                    customerId    = customerId,
                    mobileNumber  = "",
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

    /**
     * Calls UpdatePassword endpoint.
     */
    override suspend fun updatePassword(
        customerId: String,
        oldPassword: String,
        newPassword: String,
        captchaToken: String
    ): Result<String> {
        return try {
            val response = authApiService.updatePassword(
                UpdatePasswordRequestDto(
                    customerId = customerId,
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                    captchaToken = captchaToken
                )
            )
            if (response.status) {
                Result.Success(response.message ?: "Password updated successfully")
            } else {
                Result.Error(Exception(response.message ?: "Failed to update password"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
