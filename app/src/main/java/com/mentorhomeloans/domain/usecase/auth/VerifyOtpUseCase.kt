package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.common.Constants
import com.mentorhomeloans.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for verifying the OTP entered by the user during login.
 *
 * Validates OTP length before repository call. On success, the session
 * tokens are persisted by the repository/SessionManager layer.
 *
 * @param authRepository The data source for authentication operations.
 */
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Verifies the OTP for the given mobile/session combination.
     *
     * Business rules:
     * - OTP must be exactly [Constants.OTP_LENGTH] digits.
     * - OTP must contain only numeric characters.
     *
     * @param mobileNumber  The mobile number used to request the OTP.
     * @param otp           The OTP string entered by the user.
     * @param sessionId     The session reference ID returned by SendOtpUseCase.
     * @return [Result.Success] with true on successful verification,
     *         [Result.Error] on invalid OTP format or backend rejection.
     */
    suspend operator fun invoke(
        mobileNumber: String,
        otp: String,
        sessionId: String
    ): Result<Boolean> {
        if (otp.length != Constants.OTP_LENGTH || !otp.all { it.isDigit() }) {
            return Result.Error(IllegalArgumentException("Please enter a valid ${Constants.OTP_LENGTH}-digit OTP"))
        }
        return authRepository.verifyOtp(mobileNumber, otp, sessionId)
    }
}
