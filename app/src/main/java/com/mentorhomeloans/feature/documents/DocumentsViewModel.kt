package com.mentorhomeloans.feature.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.repository.DocumentRepository
import com.mentorhomeloans.domain.repository.LoanRepository
import com.mentorhomeloans.domain.usecase.document.GetDocumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.mentorhomeloans.core.datastore.UserPreferencesDataStore

/**
 * DocumentsViewModel managing master loan documents and document request actions.
 */
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val documentRepository: DocumentRepository,
    private val loanRepository: LoanRepository,
    private val sessionManager: SessionManager,
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentsUIState>(DocumentsUIState.Loading)
    val uiState: StateFlow<DocumentsUIState> = _uiState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent.asSharedFlow()

    private val _requestingDocumentId = MutableStateFlow<Int?>(null)
    val requestingDocumentId: StateFlow<Int?> = _requestingDocumentId.asStateFlow()

    private var currentLoanAcNo: String = ""
    private var masterDocuments: List<Document> = emptyList()

    init {
        loadData()
        observeSelectedLoan()
    }

    private fun observeSelectedLoan() {
        viewModelScope.launch {
            preferencesDataStore.userPreferencesFlow.collect { prefs ->
                if (prefs.selectedLoanAcNo.isNotBlank() && prefs.selectedLoanAcNo != currentLoanAcNo) {
                    currentLoanAcNo = prefs.selectedLoanAcNo
                    if (_uiState.value is DocumentsUIState.Success) {
                        _uiState.value = DocumentsUIState.Success(
                            documents = masterDocuments,
                            loanAcNo = currentLoanAcNo
                        )
                    }
                }
            }
        }
    }

    fun loadData() {
        val customerId = sessionManager.getCustomerId() ?: ""
        viewModelScope.launch {
            _uiState.value = DocumentsUIState.Loading

            // Read selected loan account number from preferences
            val savedPrefs = preferencesDataStore.userPreferencesFlow.first()
            if (savedPrefs.selectedLoanAcNo.isNotBlank()) {
                currentLoanAcNo = savedPrefs.selectedLoanAcNo
            } else {
                // Fetch loan details to obtain selected account number fallback
                loanRepository.getAllLoanAccounts(customerId).firstOrNull { it !is Result.Loading }?.let { result ->
                    if (result is Result.Success && result.data.isNotEmpty()) {
                        currentLoanAcNo = result.data.first().accountNumber
                    }
                }
            }

            // Fetch master documents list from GetMasterRecords API
            getDocumentsUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        masterDocuments = result.data
                        _uiState.value = DocumentsUIState.Success(
                            documents = masterDocuments,
                            loanAcNo = currentLoanAcNo
                        )
                    }
                    is Result.Error -> _uiState.value = DocumentsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = DocumentsUIState.Loading
                }
            }
        }
    }

    fun requestDocument(document: Document) {
        val customerId = sessionManager.getCustomerId() ?: ""
        viewModelScope.launch {
            _requestingDocumentId.value = document.id

            // Ensure loan account number is retrieved if empty
            if (currentLoanAcNo.isBlank()) {
                val savedPrefs = preferencesDataStore.userPreferencesFlow.first()
                if (savedPrefs.selectedLoanAcNo.isNotBlank()) {
                    currentLoanAcNo = savedPrefs.selectedLoanAcNo
                } else {
                    val loanResult = loanRepository.getAllLoanAccounts(customerId).firstOrNull { it !is Result.Loading }
                    if (loanResult is Result.Success && loanResult.data.isNotEmpty()) {
                        currentLoanAcNo = loanResult.data.first().accountNumber
                    }
                }
            }

            when (val result = documentRepository.requestDocument(
                customerId = customerId,
                loanAcNo = currentLoanAcNo,
                documentTypeId = document.id
            )) {
                is Result.Success -> _messageEvent.emit(result.data)
                is Result.Error -> _messageEvent.emit(result.message)
                else -> Unit
            }
            _requestingDocumentId.value = null
        }
    }
}
