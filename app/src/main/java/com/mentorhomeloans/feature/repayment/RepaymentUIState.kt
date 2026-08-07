package com.mentorhomeloans.feature.repayment

import com.mentorhomeloans.domain.model.RepaymentSummary

/**
 * UI State definition for Repayment details.
 */
sealed interface RepaymentUIState {
    object Loading : RepaymentUIState
    data class Success(val summary: RepaymentSummary) : RepaymentUIState
    data class Error(val message: String) : RepaymentUIState
}
