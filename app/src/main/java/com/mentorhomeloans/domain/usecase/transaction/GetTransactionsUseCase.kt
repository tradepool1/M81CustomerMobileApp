package com.mentorhomeloans.domain.usecase.transaction

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching paginated transaction history.
 *
 * @param transactionRepository Data source for transaction records.
 */
class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    /**
     * Returns a [Flow] of paginated transactions for the given loan account.
     *
     * @param loanAccountId The loan account ID.
     * @param page          Page number (starts at 1).
     * @param pageSize      Number of records per page.
     * @return [Flow]<[Result]<List<[Transaction]>>>.
     */
    operator fun invoke(
        loanAccountId: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Result<List<Transaction>>> {
        return transactionRepository.getTransactions(loanAccountId, page, pageSize)
    }
}
