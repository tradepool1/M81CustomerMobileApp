package com.mentorhomeloans.feature.repayment

import com.mentorhomeloans.domain.model.LoanRepaymentDetail

/**
 * UI State definition for Repayment details screen.
 */
sealed interface RepaymentUIState {
    object Loading : RepaymentUIState
    data class Success(val items: List<LoanRepaymentDetail>) : RepaymentUIState
    data class Error(val message: String) : RepaymentUIState
}

