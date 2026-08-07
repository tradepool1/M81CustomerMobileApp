package com.mentorhomeloans.feature.statements

import com.mentorhomeloans.domain.model.Statement

/**
 * State parameters for statements listing screen representation.
 */
sealed interface StatementsUIState {
    object Loading : StatementsUIState
    data class Success(val statements: List<Statement>, val allStatements: List<Statement> = statements) : StatementsUIState
    data class Error(val message: String) : StatementsUIState
}
