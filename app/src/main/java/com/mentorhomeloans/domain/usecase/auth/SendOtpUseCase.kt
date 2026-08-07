package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.utils.ValidationUtils
import com.mentorhomeloans.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for initiating an OTP request for mobile-based login.
 *
 * Validates the mobile number format before making the repository call.
 * Single-responsibility: handles ONLY the "send OTP" business rule.
 *
 * @param authRepository The data source for authentication operations.
 */
class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Invokes the use case to send an OTP to the given mobile number.
     *
     * Business rules:
     * - Mobile number must be exactly 10 digits.
     * - Mobile number must start with 6, 7, 8, or 9 (Indian numbers only).
     *
     * @param mobileNumber The 10-digit mobile number entered by the user.
     * @return [Result.Success] with the session reference ID string on success,
     *         [Result.Error] with a validation exception if the number is invalid,
     *         or a network/API error from the repository.
     */
    suspend operator fun invoke(mobileNumber: String): Result<String> {
        val validationError = ValidationUtils.validateMobileNumber(mobileNumber)
        if (validationError != null) {
            return Result.Error(IllegalArgumentException(validationError))
        }
        return authRepository.sendOtp(mobileNumber)
    }
}
