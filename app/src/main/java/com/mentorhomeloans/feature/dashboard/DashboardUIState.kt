package com.mentorhomeloans.feature.dashboard

import com.mentorhomeloans.domain.model.LoanAccount

/**
 * UI state representation for Dashboard rendering.
 */
sealed interface DashboardUIState {
    object Loading : DashboardUIState
    data class Success(val selectedLoan: LoanAccount, val allLoans: List<LoanAccount>) : DashboardUIState
    data class Error(val message: String) : DashboardUIState
}
