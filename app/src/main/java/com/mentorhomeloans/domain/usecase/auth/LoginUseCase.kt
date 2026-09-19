package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for authenticating a customer using credentials and a reCAPTCHA token.
 */
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Executes the login request.
     *
     * @param customerId   The customer's identifier.
     * @param password     The user's password.
     * @param captchaToken The token retrieved from Google reCAPTCHA.
     * @return [Result.Success] with true if authenticated, [Result.Error] otherwise.
     */
    suspend operator fun invoke(
        customerId: String,
        password: String,
        captchaToken: String
    ): Result<Boolean> {
        return repository.login(customerId, password, captchaToken)
    }
}
