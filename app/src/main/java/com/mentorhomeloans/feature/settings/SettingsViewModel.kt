package com.mentorhomeloans.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel mapping logout operations.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUIState>(SettingsUIState.Idle)
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.value = SettingsUIState.Loading
            when (val result = logoutUseCase()) {
                is Result.Success -> _uiState.value = SettingsUIState.LogoutSuccess
                is Result.Error -> _uiState.value = SettingsUIState.Error(result.message)
                else -> Unit
            }
        }
    }
}
