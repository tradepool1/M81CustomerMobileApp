package com.mentorhomeloans.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.auth.SendOtpUseCase
import com.mentorhomeloans.domain.usecase.auth.VerifyOtpUseCase
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel mapping authentication states.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.EnterMobile)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun sendOtp(mobileNumber: String, branchCode: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading
            when (val result = sendOtpUseCase(mobileNumber)) {
                is Result.Success -> _uiState.value = LoginUIState.VerifyOtp(result.data)
                is Result.Error -> _uiState.value = LoginUIState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun verifyOtp(mobileNumber: String, otp: String, sessionId: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading
            when (val result = verifyOtpUseCase(mobileNumber, otp, sessionId)) {
                is Result.Success -> {
                    preferencesDataStore.setOnboardingCompleted(true)
                    _uiState.value = LoginUIState.Success("CUST00123")
                }
                is Result.Error -> _uiState.value = LoginUIState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUIState.EnterMobile
    }
}
