package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock Auth Repository providing sample verification mechanisms.
 */
@Singleton
class MockAuthRepository @Inject constructor(
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun sendOtp(mobileNumber: String): Result<String> {
        delay(1000)
        return Result.Success("mock_session_id_xyz")
    }

    override suspend fun verifyOtp(
        mobileNumber: String,
        otp: String,
        sessionId: String
    ): Result<Boolean> {
        delay(1000)
        if (otp == "123456") {
            sessionManager.saveSession(
                jwtToken = "mock_jwt_token_header",
                refreshToken = "mock_refresh_token_payload",
                customerId = "CUST00123",
                mobileNumber = mobileNumber
            )
            return Result.Success(true)
        }
        return Result.Error(IllegalArgumentException("Invalid OTP. Try 123456"))
    }

    override suspend fun logout(): Result<Unit> {
        sessionManager.clearSession()
        return Result.Success(Unit)
    }

    override fun isSessionActive(): Flow<Boolean> {
        return sessionManager.isSessionActive
    }
}
