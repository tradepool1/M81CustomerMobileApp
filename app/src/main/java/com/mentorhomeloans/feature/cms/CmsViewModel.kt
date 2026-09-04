package com.mentorhomeloans.feature.cms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.dto.PageContentDto
import com.mentorhomeloans.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CmsUIState {
    object Loading : CmsUIState
    data class Success(val page: PageContentDto) : CmsUIState
    data class Error(val message: String) : CmsUIState
}

@HiltViewModel
class CmsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CmsUIState>(CmsUIState.Loading)
    val uiState: StateFlow<CmsUIState> = _uiState.asStateFlow()

    fun loadPageContent(pageKey: String) {
        viewModelScope.launch {
            _uiState.value = CmsUIState.Loading
            when (val result = authRepository.getPageContent(pageKey)) {
                is Result.Success -> {
                    _uiState.value = CmsUIState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = CmsUIState.Error(result.exception.message ?: "Unknown error")
                }
                Result.Loading -> {
                    _uiState.value = CmsUIState.Loading
                }
            }
        }
    }
}
