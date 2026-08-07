package com.mentorhomeloans.domain.usecase.loan

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching all loan accounts for the customer.
 */
class GetLoanAccountsUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    operator fun invoke(customerId: String): Flow<Result<List<LoanAccount>>> {
        return loanRepository.getAllLoanAccounts(customerId)
    }
}
