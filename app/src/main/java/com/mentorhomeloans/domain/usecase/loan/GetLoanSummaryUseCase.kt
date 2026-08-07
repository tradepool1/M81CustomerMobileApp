package com.mentorhomeloans.domain.usecase.loan

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Use case for forcing a network refresh of loan summary data.
 *
 * Called during pull-to-refresh on the Dashboard or when the user
 * explicitly wants the latest data from the server.
 *
 * @param loanRepository Data source for loan account information.
 */
class GetLoanSummaryUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Forces a refresh of the loan account data from the remote API.
     *
     * @param customerId The logged-in customer's unique identifier.
     * @return [Result.Success] with Unit when refresh succeeds,
     *         [Result.Error] when offline or API error occurs.
     */
    suspend operator fun invoke(customerId: String): Result<Unit> {
        return loanRepository.refreshLoanAccount(customerId)
    }
}
