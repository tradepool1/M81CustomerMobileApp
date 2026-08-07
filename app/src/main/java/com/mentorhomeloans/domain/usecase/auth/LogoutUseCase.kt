package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging out the currently authenticated customer.
 *
 * Delegates logout (token clearing, cache invalidation) to the repository layer.
 * ViewModels should call this before navigating to the Login screen.
 *
 * @param authRepository The data source for authentication operations.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Executes the logout operation.
     *
     * Business logic:
     * - Clears JWT and refresh tokens from secure storage.
     * - Invalidates local database cache.
     *
     * @return [Result.Success] with Unit on success, [Result.Error] on failure.
     */
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
