package com.mentorhomeloans.feature.statements

import com.mentorhomeloans.domain.model.LoanSOADetail
import com.mentorhomeloans.domain.model.Statement

/**
 * State parameters for statements and Statement of Account (SOA) listing screen representation.
 */
sealed interface StatementsUIState {
    object Loading : StatementsUIState

    data class Success(
        val statements: List<Statement>,
        val allStatements: List<Statement> = statements,
        val soaDetails: List<LoanSOADetail> = emptyList(),
        val filteredSoaDetails: List<LoanSOADetail> = soaDetails,
        val totalDebit: Double = 0.0,
        val totalCredit: Double = 0.0,
        val netBalance: Double = 0.0,
        val searchQuery: String = ""
    ) : StatementsUIState

    data class Error(val message: String) : StatementsUIState
}
