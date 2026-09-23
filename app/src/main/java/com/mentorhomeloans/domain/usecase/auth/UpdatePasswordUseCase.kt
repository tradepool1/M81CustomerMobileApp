package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case to update customer password.
 */
class UpdatePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        customerId: String,
        oldPassword: String,
        newPassword: String,
        captchaToken: String
    ): Result<String> {
        return repository.updatePassword(customerId, oldPassword, newPassword, captchaToken)
    }
}
