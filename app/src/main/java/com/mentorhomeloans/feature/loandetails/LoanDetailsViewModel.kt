package com.mentorhomeloans.feature.loandetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.usecase.loan.GetLoanDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoanDetailsUIState {
    object Loading : LoanDetailsUIState
    data class Success(val loan: LoanAccount) : LoanDetailsUIState
    data class Error(val message: String) : LoanDetailsUIState
}

@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getLoanDetailsUseCase: GetLoanDetailsUseCase
) : ViewModel() {

    private val loanId: String = checkNotNull(savedStateHandle["loanId"])

    private val _uiState = MutableStateFlow<LoanDetailsUIState>(LoanDetailsUIState.Loading)
    val uiState: StateFlow<LoanDetailsUIState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = LoanDetailsUIState.Loading
            getLoanDetailsUseCase(loanId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = LoanDetailsUIState.Success(result.data)
                    is Result.Error -> _uiState.value = LoanDetailsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = LoanDetailsUIState.Loading
                }
            }
        }
    }
}
