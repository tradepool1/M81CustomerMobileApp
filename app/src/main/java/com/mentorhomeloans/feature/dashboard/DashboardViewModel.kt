package com.mentorhomeloans.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.loan.GetLoanAccountsUseCase
import com.mentorhomeloans.domain.usecase.loan.GetLoanSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mentorhomeloans.domain.model.LoanAccount

/**
 * DashboardViewModel managing loan accounts summaries caching states.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getLoanAccountsUseCase: GetLoanAccountsUseCase,
    private val getLoanSummaryUseCase: GetLoanSummaryUseCase
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
            getLoanAccountsUseCase("CUST00123").collect { result ->
                when (result) {
                    is Result.Success -> {
                        allLoansList = result.data
                        if (allLoansList.isNotEmpty()) {
                            // Ensure selected index is within bounds
                            if (currentSelectedIndex >= allLoansList.size) {
                                currentSelectedIndex = 0
                            }
                            _uiState.value = DashboardUIState.Success(allLoansList[currentSelectedIndex], allLoansList)
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
            _uiState.value = DashboardUIState.Success(allLoansList[currentSelectedIndex], allLoansList)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            getLoanSummaryUseCase("CUST00123")
        }
    }
}
