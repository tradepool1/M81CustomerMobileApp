package com.mentorhomeloans.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ProfileViewModel fetching profile values.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Loading)
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUIState.Loading
            getProfileUseCase("CUST00123").collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = ProfileUIState.Success(result.data)
                    is Result.Error -> _uiState.value = ProfileUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = ProfileUIState.Loading
                }
            }
        }
    }
}
