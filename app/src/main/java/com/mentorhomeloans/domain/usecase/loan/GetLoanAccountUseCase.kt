package com.mentorhomeloans.domain.usecase.loan

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching the customer's loan account details.
 *
 * Used primarily by the Dashboard and throughout the app wherever
 * loan account details are needed.
 *
 * @param loanRepository Data source for loan account information.
 */
class GetLoanAccountUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Returns a [Flow] of the loan account for the given customer.
     *
     * Follows offline-first: emits cached Room data immediately,
     * then updates from network in the background.
     *
     * @param customerId The logged-in customer's unique identifier.
     * @return [Flow]<[Result]<[LoanAccount]>> with real-time updates.
     */
    operator fun invoke(customerId: String): Flow<Result<LoanAccount>> {
        return loanRepository.getLoanAccount(customerId)
    }
}
