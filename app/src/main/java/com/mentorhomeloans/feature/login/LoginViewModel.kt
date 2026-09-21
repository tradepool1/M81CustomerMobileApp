package com.mentorhomeloans.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.auth.LoginUseCase
import com.mentorhomeloans.domain.usecase.auth.UpdatePasswordUseCase
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel mapping authentication states using reCAPTCHA and credentials.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Initial)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _captchaTrigger = MutableSharedFlow<Unit>()
    val captchaTrigger: SharedFlow<Unit> = _captchaTrigger.asSharedFlow()

    /**
     * Called by the UI when the user clicks the Login button.
     * Initiates the CAPTCHA flow.
     */
    fun startLoginFlow() {
        viewModelScope.launch {
            // Immediately show loading to indicate background work started
            _uiState.value = LoginUIState.CaptchaLoading
            _captchaTrigger.emit(Unit)
        }
    }

    /**
     * Called by the UI after successfully retrieving a reCAPTCHA token.
     */
    fun onCaptchaSuccess(token: String, customerId: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.LoginLoading
            when (val result = loginUseCase(customerId, password, token)) {
                is Result.Success -> {
                    preferencesDataStore.setOnboardingCompleted(true)
                    _uiState.value = LoginUIState.Success(customerId)
                }
                is Result.Error -> _uiState.value = LoginUIState.Error(result.message)
                else -> Unit
            }
        }
    }

    /**
     * Called by the UI if the reCAPTCHA challenge fails or is cancelled.
     */
    fun onCaptchaError(errorMsg: String) {
        _uiState.value = LoginUIState.Error("CAPTCHA error: $errorMsg")
    }

    fun updatePassword(customerId: String, password: String, captchaToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.LoginLoading
            when (val result = updatePasswordUseCase(customerId, password, captchaToken)) {
                is Result.Success -> {
                    _toastEvent.emit(result.data)
                    _uiState.value = LoginUIState.Initial
                }
                is Result.Error -> _uiState.value = LoginUIState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun showUpdatePassword() {
        _uiState.value = LoginUIState.UpdatePassword
    }

    fun resetState() {
        _uiState.value = LoginUIState.Initial
    }
}
