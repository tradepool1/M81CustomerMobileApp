package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for transaction history operations.
 *
 * Provides access to EMI payment history, charge records, and supports
 * filtering and searching capabilities.
 */
interface TransactionRepository {

    /**
     * Observes the transaction history for a given loan account.
     *
     * Follows the offline-first pattern: emits cached data immediately,
     * then loads fresh data from the network.
     *
     * @param loanAccountId The loan account ID to fetch transactions for.
     * @param page          Page number for pagination (starts at 1).
     * @param pageSize      Number of transactions per page.
     * @return A [Flow] emitting paginated lists of [Transaction].
     */
    fun getTransactions(
        loanAccountId: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Result<List<Transaction>>>

    /**
     * Filters transactions by a date range and/or transaction type.
     *
     * @param loanAccountId The loan account to filter.
     * @param fromDate      Start date filter (yyyy-MM-dd). Null means no lower bound.
     * @param toDate        End date filter (yyyy-MM-dd). Null means today.
     * @param types         List of [TransactionType] to include. Empty means all types.
     * @return [Result.Success] with filtered list, [Result.Error] on failure.
     */
    suspend fun filterTransactions(
        loanAccountId: String,
        fromDate: String?,
        toDate: String?,
        types: List<TransactionType>
    ): Result<List<Transaction>>

    /**
     * Searches transactions by reference number, description, or amount.
     *
     * @param loanAccountId The loan account to search within.
     * @param query         The search query string.
     * @return [Result.Success] with matching transactions, [Result.Error] on failure.
     */
    suspend fun searchTransactions(
        loanAccountId: String,
        query: String
    ): Result<List<Transaction>>
}
