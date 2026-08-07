package com.mentorhomeloans.domain.usecase.transaction

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for searching transactions by free-text query.
 *
 * @param transactionRepository Data source for transaction records.
 */
class SearchTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    /**
     * Searches for transactions matching the given [query].
     *
     * Business rule: query must be at least 2 characters to avoid
     * excessively broad server-side searches.
     *
     * @param loanAccountId The loan account to search within.
     * @param query         The free-text search string.
     * @return [Result.Success] with matching transactions, or
     *         [Result.Error] with validation or API error.
     */
    suspend operator fun invoke(loanAccountId: String, query: String): Result<List<Transaction>> {
        if (query.trim().length < 2) {
            return Result.Error(IllegalArgumentException("Search query must be at least 2 characters"))
        }
        return transactionRepository.searchTransactions(loanAccountId, query.trim())
    }
}
