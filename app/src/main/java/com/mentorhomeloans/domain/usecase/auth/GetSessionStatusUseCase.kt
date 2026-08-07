package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing the current authentication session status.
 *
 * Used by the splash screen and navigation graph to determine whether
 * to route the user to the Login screen or directly to the Dashboard.
 *
 * @param authRepository The data source for authentication state.
 */
class GetSessionStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Returns a [Flow] that emits the current session validity.
     *
     * - Emits `true`  when a valid JWT session exists.
     * - Emits `false` when the user is logged out or the token has expired.
     *
     * @return [Flow]<Boolean> representing real-time session state.
     */
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isSessionActive()
    }
}
