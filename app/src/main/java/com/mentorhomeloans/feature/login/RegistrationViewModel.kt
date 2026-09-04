package com.mentorhomeloans.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.dto.BranchDto
import com.mentorhomeloans.data.remote.dto.StateDto
import com.mentorhomeloans.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the registration screen. */
sealed class RegistrationUiState {
    object Idle    : RegistrationUiState()
    object Loading : RegistrationUiState()
    data class Success(val message: String) : RegistrationUiState()
    data class Error(val message: String)   : RegistrationUiState()
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    // -- States dropdown -------------------------------------------------------
    private val _states = MutableStateFlow<List<StateDto>>(emptyList())
    val states: StateFlow<List<StateDto>> = _states.asStateFlow()

    private val _statesLoading = MutableStateFlow(false)
    val statesLoading: StateFlow<Boolean> = _statesLoading.asStateFlow()

    // -- Branches dropdown -----------------------------------------------------
    private val _branches = MutableStateFlow<List<BranchDto>>(emptyList())
    val branches: StateFlow<List<BranchDto>> = _branches.asStateFlow()

    private val _branchesLoading = MutableStateFlow(false)
    val branchesLoading: StateFlow<Boolean> = _branchesLoading.asStateFlow()

    // -- Submit state ----------------------------------------------------------
    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    init {
        loadStates()
    }

    /** Fetch states from API on screen load. */
    fun loadStates() {
        viewModelScope.launch {
            _statesLoading.value = true
            when (val result = registrationRepository.getStates()) {
                is Result.Success -> {
                    _states.value = result.data
                    // Auto-select first state and load its branches
                    result.data.firstOrNull()?.let { loadBranches(it.stateId) }
                }
                is Result.Error -> {
                    _uiState.value = RegistrationUiState.Error(
                        result.message.ifBlank { "Failed to load states." }
                    )
                }
                else -> Unit
            }
            _statesLoading.value = false
        }
    }

    /** Called whenever user selects a state. Fetches branches for [stateId]. */
    fun loadBranches(stateId: Int) {
        viewModelScope.launch {
            _branchesLoading.value = true
            _branches.value = emptyList()
            when (val result = registrationRepository.getBranchesByStateId(stateId)) {
                is Result.Success -> _branches.value = result.data
                is Result.Error   -> {
                    _uiState.value = RegistrationUiState.Error(
                        result.message.ifBlank { "Failed to load branches." }
                    )
                }
                else -> Unit
            }
            _branchesLoading.value = false
        }
    }

    /** Submit the registration form. */
    fun submitRegistration(
        name: String,
        email: String,
        mobileNo: String,
        stateId: Int,
        branchId: Int,
        loanAcNo: String,
        panNo: String
    ) {
        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading
            when (val result = registrationRepository.registerCustomer(
                name, email, mobileNo, stateId, branchId, loanAcNo, panNo
            )) {
                is Result.Success -> _uiState.value = RegistrationUiState.Success(result.data)
                is Result.Error   -> _uiState.value = RegistrationUiState.Error(
                    result.message.ifBlank { "Registration failed. Please try again." }
                )
                else -> Unit
            }
        }
    }

    fun resetState() {
        _uiState.value = RegistrationUiState.Idle
    }
}
