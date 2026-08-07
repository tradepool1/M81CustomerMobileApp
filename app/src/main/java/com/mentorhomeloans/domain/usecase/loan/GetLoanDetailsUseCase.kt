package com.mentorhomeloans.domain.usecase.loan

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching a specific loan account by its ID.
 */
class GetLoanDetailsUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    operator fun invoke(loanId: String): Flow<Result<LoanAccount>> {
        return loanRepository.getLoanById(loanId)
    }
}
