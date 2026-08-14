package com.mentorhomeloans.feature.repayment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.repayment.GetLoanRepaymentDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RepaymentViewModel managing loan repayment schedule from the remote API.
 *
 * The [loanId] is extracted from the navigation back-stack entry via
 * [SavedStateHandle] – it matches the {loanId} argument in Screen.Repayment.
 */
@HiltViewModel
class RepaymentViewModel @Inject constructor(
    private val getLoanRepaymentDetailsUseCase: GetLoanRepaymentDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val loanId: String = checkNotNull(savedStateHandle["loanId"])

    private val _uiState = MutableStateFlow<RepaymentUIState>(RepaymentUIState.Loading)
    val uiState: StateFlow<RepaymentUIState> = _uiState.asStateFlow()

    init {
        loadRepaymentDetails()
    }

    fun loadRepaymentDetails() {
        viewModelScope.launch {
            _uiState.value = RepaymentUIState.Loading
            getLoanRepaymentDetailsUseCase(loanId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = RepaymentUIState.Success(result.data)
                    is Result.Error   -> _uiState.value = RepaymentUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = RepaymentUIState.Loading
                }
            }
        }
    }
}

