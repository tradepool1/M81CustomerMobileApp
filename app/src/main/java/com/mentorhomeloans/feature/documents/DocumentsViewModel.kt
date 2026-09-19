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

/**
 * DocumentsViewModel managing document downloads and lists status.
 */
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val documentRepository: DocumentRepository,
    private val loanRepository: LoanRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentsUIState>(DocumentsUIState.Loading)
    val uiState: StateFlow<DocumentsUIState> = _uiState.asStateFlow()

    private val _downloadEvent = MutableSharedFlow<String>()
    val downloadEvent: SharedFlow<String> = _downloadEvent.asSharedFlow()

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        val customerId = sessionManager.getCustomerId() ?: ""
        viewModelScope.launch {
            _uiState.value = DocumentsUIState.Loading
            getDocumentsUseCase(customerId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = DocumentsUIState.Success(result.data)
                    is Result.Error -> _uiState.value = DocumentsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = DocumentsUIState.Loading
                }
            }
        }
    }

    fun downloadDocument(document: Document) {
        val customerId = sessionManager.getCustomerId() ?: ""
        viewModelScope.launch {
            // 1. Fetch loan account to get account number
            val loanResult = loanRepository.getLoanAccount(customerId).firstOrNull { it !is Result.Loading }
            
            if (loanResult is Result.Success) {
                val loanAcNo = loanResult.data.accountNumber
                // 2. Call the server API for GetLoanDocuments
                when (val result = documentRepository.getLoanDocuments(loanAcNo)) {
                    is Result.Success -> _downloadEvent.emit(result.data)
                    is Result.Error -> _downloadEvent.emit("Download failed: ${result.message}")
                    else -> Unit
                }
            } else {
                _downloadEvent.emit("Unable to fetch loan details for download")
            }
        }
    }
}
