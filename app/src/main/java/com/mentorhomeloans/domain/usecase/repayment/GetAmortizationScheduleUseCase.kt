package com.mentorhomeloans.domain.usecase.repayment

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.RepaymentSummary
import com.mentorhomeloans.domain.repository.RepaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching the loan amortization schedule and repayment summary.
 *
 * @param repaymentRepository Data source for repayment information.
 */
class GetAmortizationScheduleUseCase @Inject constructor(
    private val repaymentRepository: RepaymentRepository
) {
    /**
     * Returns a [Flow] of the complete repayment summary including the
     * full amortization table.
     *
     * @param loanAccountId The loan account identifier.
     * @return [Flow]<[Result]<[RepaymentSummary]>>.
     */
    operator fun invoke(loanAccountId: String): Flow<Result<RepaymentSummary>> {
        return repaymentRepository.getRepaymentSummary(loanAccountId)
    }
}
