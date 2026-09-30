package com.mentorhomeloans.domain.usecase.auth

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.dto.SaveFcmTokenResponseDto
import com.mentorhomeloans.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for saving FCM token on the server.
 */
class SaveFcmTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Executes the SaveFcmToken API call.
     *
     * @param fcmToken The FCM token string to register (defaults to "abcd" for testing).
     * @return [Result.Success] containing response list, or [Result.Error].
     */
    suspend operator fun invoke(fcmToken: String = "abcd"): Result<List<SaveFcmTokenResponseDto>> {
        return repository.saveFcmToken(fcmToken)
    }
}
