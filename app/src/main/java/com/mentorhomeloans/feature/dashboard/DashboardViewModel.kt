package com.mentorhomeloans.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.usecase.loan.GetLoanAccountsUseCase
import com.mentorhomeloans.domain.usecase.loan.GetLoanSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.first

/**
 * DashboardViewModel managing loan accounts summaries caching states.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getLoanAccountsUseCase: GetLoanAccountsUseCase,
    private val getLoanSummaryUseCase: GetLoanSummaryUseCase,
    private val sessionManager: SessionManager,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUIState>(DashboardUIState.Loading)
    val uiState: StateFlow<DashboardUIState> = _uiState.asStateFlow()

    private var allLoansList: List<LoanAccount> = emptyList()
    private var currentSelectedIndex = 0

    init {
        loadLoanData()
    }

    fun loadLoanData() {
        viewModelScope.launch {
            _uiState.value = DashboardUIState.Loading
            val customerId = sessionManager.getCustomerId() ?: ""
            getLoanAccountsUseCase(customerId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        allLoansList = result.data
                        if (allLoansList.isNotEmpty()) {
                            // Check if a loan was previously selected in preferences
                            val savedPrefs = preferencesDataStore.userPreferencesFlow.first()
                            val savedAcNo = savedPrefs.selectedLoanAcNo
                            val foundIndex = allLoansList.indexOfFirst { it.accountNumber == savedAcNo }

                            currentSelectedIndex = if (foundIndex >= 0) foundIndex else 0

                            val selectedLoan = allLoansList[currentSelectedIndex]
                            preferencesDataStore.setSelectedLoan(selectedLoan.accountNumber, selectedLoan.id)

                            _uiState.value = DashboardUIState.Success(selectedLoan, allLoansList)
                        } else {
                            _uiState.value = DashboardUIState.Error("No loans found")
                        }
                    }
                    is Result.Error -> _uiState.value = DashboardUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = DashboardUIState.Loading
                }
            }
        }
    }

    fun selectLoan(index: Int) {
        if (allLoansList.isNotEmpty() && index in allLoansList.indices) {
            currentSelectedIndex = index
            val selectedLoan = allLoansList[currentSelectedIndex]
            _uiState.value = DashboardUIState.Success(selectedLoan, allLoansList)
            viewModelScope.launch {
                preferencesDataStore.setSelectedLoan(selectedLoan.accountNumber, selectedLoan.id)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val customerId = sessionManager.getCustomerId() ?: ""
            getLoanSummaryUseCase(customerId)
        }
    }
}
