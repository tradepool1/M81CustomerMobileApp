package com.mentorhomeloans.feature.repayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.repayment.GetAmortizationScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RepaymentViewModel managing schedule updates and states.
 */
@HiltViewModel
class RepaymentViewModel @Inject constructor(
    private val getAmortizationScheduleUseCase: GetAmortizationScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepaymentUIState>(RepaymentUIState.Loading)
    val uiState: StateFlow<RepaymentUIState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            _uiState.value = RepaymentUIState.Loading
            getAmortizationScheduleUseCase("loan_99120").collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = RepaymentUIState.Success(result.data)
                    is Result.Error -> _uiState.value = RepaymentUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = RepaymentUIState.Loading
                }
            }
        }
    }
}
