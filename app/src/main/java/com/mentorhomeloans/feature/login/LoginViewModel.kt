package com.mentorhomeloans.feature.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.security.captcha.CaptchaManager
import com.mentorhomeloans.core.security.captcha.CaptchaResult
import com.mentorhomeloans.domain.usecase.auth.LoginUseCase
import com.mentorhomeloans.domain.usecase.auth.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel coordinating authentication states, CAPTCHA token generation, and credentials.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val captchaManager: CaptchaManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Initial)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _captchaTrigger = MutableSharedFlow<Unit>()
    val captchaTrigger: SharedFlow<Unit> = _captchaTrigger.asSharedFlow()

    /**
     * Testing flag: when true, prints/logs the generated CAPTCHA token and does NOT call
     * the ValidateCustomerCredentials backend API. Defaults to false for production execution.
     */
    var bypassApiForTesting: Boolean = false

    /**
     * Executes login:
     * 1. Validates non-empty input.
     * 2. Sets uiState to CaptchaLoading.
     * 3. Generates a fresh CAPTCHA token with explicit action "LOGIN".
     * 4. Fails gracefully if CAPTCHA token generation fails or token is empty.
     * 5. Logs the token and (if not bypassed) calls loginUseCase.
     */
    fun login(customerId: String, password: String) {
        if (customerId.isBlank() || password.isBlank()) {
            _uiState.value = LoginUIState.Error("Please enter your Customer ID and Password")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUIState.CaptchaLoading
            when (val captchaResult = captchaManager.generateToken(CaptchaManager.ACTION_LOGIN)) {
                is CaptchaResult.Success -> {
                    val token = captchaResult.token
                    if (token.isBlank()) {
                        _uiState.value = LoginUIState.Error("CAPTCHA error: Security check returned an empty token.")
                        return@launch
                    }

                    if (bypassApiForTesting) {
                        _toastEvent.emit("CAPTCHA Token Generated! (API bypassed for testing)")
                        _uiState.value = LoginUIState.Initial
                        return@launch
                    }

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
                is CaptchaResult.Failure -> {
                    _uiState.value = LoginUIState.Error("CAPTCHA error: ${captchaResult.error.message}")
                }
            }
        }
    }

    /**
     * Legacy trigger for UI-driven CAPTCHA flow.
     */
    fun startLoginFlow() {
        viewModelScope.launch {
            _uiState.value = LoginUIState.CaptchaLoading
            _captchaTrigger.emit(Unit)
        }
    }

    /**
     * Called when a valid reCAPTCHA token is obtained.
     */
    fun onCaptchaSuccess(token: String, customerId: String, password: String) {
        if (token.isBlank()) {
            _uiState.value = LoginUIState.Error("CAPTCHA error: Empty token received")
            return
        }

        Log.d("CAPTCHA_TOKEN", "==================================================")
        Log.d("CAPTCHA_TOKEN", "CAPTCHA Token Generated Successfully (onCaptchaSuccess):")
        Log.d("CAPTCHA_TOKEN", token)
        Log.d("CAPTCHA_TOKEN", "==================================================")
        println("CAPTCHA_TOKEN: $token")

        if (bypassApiForTesting) {
            viewModelScope.launch {
                _toastEvent.emit("CAPTCHA Token Generated! (API bypassed for testing)")
                _uiState.value = LoginUIState.Initial
            }
            return
        }

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
     * Called if reCAPTCHA challenge fails or is cancelled.
     */
    fun onCaptchaError(errorMsg: String) {
        _uiState.value = LoginUIState.Error("CAPTCHA error: $errorMsg")
    }

    fun updatePassword(customerId: String, oldPassword: String, newPassword: String, captchaToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.LoginLoading
            when (val result = updatePasswordUseCase(customerId, oldPassword, newPassword, captchaToken)) {
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
