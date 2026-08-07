package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanAccount
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for loan account data operations.
 *
 * All loan account fetching and retrieval is routed through this contract.
 * The offline-first pattern means data flows from Room → UI and network
 * updates are written to Room before being observed.
 */
interface LoanRepository {

    /**
     * Observes the active loan account for the logged-in customer.
     *
     * Emits cached data from Room immediately, then refreshes from network
     * in the background. Emits [Result.Loading] initially, then
     * [Result.Success] or [Result.Error].
     *
     * @param customerId The ID of the logged-in customer.
     * @return A [Flow] of [Result]<[LoanAccount]> with real-time updates.
     */
    fun getLoanAccount(customerId: String): Flow<Result<LoanAccount>>

    /**
     * Observes a specific loan account by its unique ID.
     *
     * @param loanId The unique ID of the loan account.
     * @return A [Flow] of [Result]<[LoanAccount]>.
     */
    fun getLoanById(loanId: String): Flow<Result<LoanAccount>>

    /**
     * Observes all active loan accounts for the logged-in customer.
     *
     * @param customerId The ID of the logged-in customer.
     * @return A [Flow] of [Result]<[List]<[LoanAccount]>> with real-time updates.
     */
    fun getAllLoanAccounts(customerId: String): Flow<Result<List<LoanAccount>>>

    /**
     * Forces a network refresh of the loan account data,
     * bypassing the local cache.
     *
     * @param customerId The ID of the logged-in customer.
     * @return [Result.Success] on successful refresh, [Result.Error] otherwise.
     */
    suspend fun refreshLoanAccount(customerId: String): Result<Unit>
}
