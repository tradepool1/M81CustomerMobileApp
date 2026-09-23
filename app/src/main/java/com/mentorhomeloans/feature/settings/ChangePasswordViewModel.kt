package com.mentorhomeloans.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Change Password screen.
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordUIState>(ChangePasswordUIState.Initial)
    val uiState: StateFlow<ChangePasswordUIState> = _uiState.asStateFlow()

    private val _captchaTrigger = MutableSharedFlow<Unit>()
    val captchaTrigger: SharedFlow<Unit> = _captchaTrigger.asSharedFlow()

    /**
     * Triggered when the user clicks Change Password.
     */
    fun startChangePasswordFlow() {
        viewModelScope.launch {
            _captchaTrigger.emit(Unit)
        }
    }

    /**
     * Called after CAPTCHA success to perform the actual update.
     */
    fun onCaptchaSuccess(token: String, oldPassword: String, newPassword: String) {
        val customerId = sessionManager.getCustomerId() ?: ""
        if (customerId.isBlank()) {
            _uiState.value = ChangePasswordUIState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _uiState.value = ChangePasswordUIState.Loading
            when (val result = authRepository.updatePassword(customerId, oldPassword, newPassword, token)) {
                is Result.Success -> {
                    _uiState.value = ChangePasswordUIState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = ChangePasswordUIState.Error(result.message)
                }
                else -> Unit
            }
        }
    }

    fun onCaptchaError(errorMsg: String) {
        _uiState.value = ChangePasswordUIState.Error("Security check failed: $errorMsg")
    }

    fun resetState() {
        _uiState.value = ChangePasswordUIState.Initial
    }
}
