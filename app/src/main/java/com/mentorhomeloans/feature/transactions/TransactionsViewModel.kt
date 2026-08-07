package com.mentorhomeloans.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.model.TransactionType
import com.mentorhomeloans.domain.usecase.transaction.GetTransactionsUseCase
import com.mentorhomeloans.domain.usecase.transaction.SearchTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TransactionsViewModel managing history logs filters query details.
 */
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionsUIState>(TransactionsUIState.Loading)
    val uiState: StateFlow<TransactionsUIState> = _uiState.asStateFlow()

    // Filter state
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    val selectedType: StateFlow<TransactionType?> = _selectedType.asStateFlow()

    private val _dateFrom = MutableStateFlow<String?>(null)
    val dateFrom: StateFlow<String?> = _dateFrom.asStateFlow()

    private val _dateTo = MutableStateFlow<String?>(null)
    val dateTo: StateFlow<String?> = _dateTo.asStateFlow()

    // Holds the full loaded list for client-side filtering
    private var allTransactions: List<Transaction> = emptyList()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = TransactionsUIState.Loading
            getTransactionsUseCase("loan_99120").collect { result ->
                when (result) {
                    is Result.Success -> {
                        allTransactions = result.data
                        applyCurrentFilters()
                    }
                    is Result.Error -> _uiState.value = TransactionsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = TransactionsUIState.Loading
                }
            }
        }
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedType.value = type
        applyCurrentFilters()
    }

    fun setDateRange(from: String?, to: String?) {
        _dateFrom.value = from
        _dateTo.value = to
        applyCurrentFilters()
    }

    fun clearFilters() {
        _selectedType.value = null
        _dateFrom.value = null
        _dateTo.value = null
        applyCurrentFilters()
    }

    fun searchTransactions(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                applyCurrentFilters()
                return@launch
            }
            _uiState.value = TransactionsUIState.Loading
            when (val result = searchTransactionsUseCase("loan_99120", query)) {
                is Result.Success -> _uiState.value = TransactionsUIState.Success(result.data)
                is Result.Error -> _uiState.value = TransactionsUIState.Error(result.message)
                else -> Unit
            }
        }
    }

    private fun applyCurrentFilters() {
        var filtered = allTransactions
        val type = _selectedType.value
        val from = _dateFrom.value
        val to = _dateTo.value

        if (type != null) {
            filtered = filtered.filter { it.type == type }
        }
        if (from != null) {
            filtered = filtered.filter { it.transactionDate >= from }
        }
        if (to != null) {
            filtered = filtered.filter { it.transactionDate <= to }
        }
        _uiState.value = TransactionsUIState.Success(filtered)
    }

    fun hasActiveFilters(): Boolean =
        _selectedType.value != null || _dateFrom.value != null || _dateTo.value != null
}
