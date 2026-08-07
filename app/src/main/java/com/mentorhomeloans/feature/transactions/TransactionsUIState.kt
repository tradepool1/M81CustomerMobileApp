package com.mentorhomeloans.feature.transactions

import com.mentorhomeloans.domain.model.Transaction

/**
 * UI State definition for Transactions history listings.
 */
sealed interface TransactionsUIState {
    object Loading : TransactionsUIState
    data class Success(val transactions: List<Transaction>) : TransactionsUIState
    data class Error(val message: String) : TransactionsUIState
}
