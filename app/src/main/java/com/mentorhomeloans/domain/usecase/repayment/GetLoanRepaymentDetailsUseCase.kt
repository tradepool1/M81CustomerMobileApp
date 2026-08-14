package com.mentorhomeloans.domain.usecase.repayment

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanRepaymentDetail
import com.mentorhomeloans.domain.repository.RepaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching the EMI-wise loan repayment details list.
 *
 * Wraps the [RepaymentRepository.getLoanRepaymentDetails] call and
 * exposes it as a clean, invokable use case for the ViewModel.
 *
 * @param repaymentRepository Data source for repayment information.
 */
class GetLoanRepaymentDetailsUseCase @Inject constructor(
    private val repaymentRepository: RepaymentRepository
) {
    /**
     * @param loanId The numeric loan ID passed as a query param to the API.
     * @return [Flow]<[Result]<[List]<[LoanRepaymentDetail]>>>.
     */
    operator fun invoke(loanId: String): Flow<Result<List<LoanRepaymentDetail>>> =
        repaymentRepository.getLoanRepaymentDetails(loanId)
}
