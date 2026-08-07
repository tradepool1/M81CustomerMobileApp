package com.mentorhomeloans.domain.usecase.profile

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.User
import com.mentorhomeloans.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the customer's profile details.
 *
 * @param profileRepository The repository for profile data.
 */
class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    /**
     * Executes the profile retrieval.
     *
     * @param customerId The unique identifier of the customer.
     * @return A [Flow] emitting [Result] of [User].
     */
    operator fun invoke(customerId: String): Flow<Result<User>> {
        return profileRepository.getProfile(customerId)
    }
}
