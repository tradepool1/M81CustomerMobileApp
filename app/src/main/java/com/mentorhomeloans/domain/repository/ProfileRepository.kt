package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for customer profile data.
 */
interface ProfileRepository {

    /**
     * Observes the logged-in customer's full profile.
     *
     * @param customerId The customer identifier.
     * @return A [Flow] of [Result]<[User]>.
     */
    fun getProfile(customerId: String): Flow<Result<User>>

    /**
     * Updates the customer's contact email address.
     *
     * @param customerId The customer identifier.
     * @param newEmail   The new email address.
     * @return [Result.Success] with Unit on success.
     */
    suspend fun updateEmail(customerId: String, newEmail: String): Result<Unit>

    /**
     * Permanently deletes the customer account.
     *
     * @param loanAcNo The loan account number to identify the account.
     * @return [Result.Success] with Unit on success.
     */
    suspend fun deleteAccount(loanAcNo: String): Result<Unit>
}
