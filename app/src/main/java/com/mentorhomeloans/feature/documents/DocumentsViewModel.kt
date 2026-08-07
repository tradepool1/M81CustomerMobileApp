package com.mentorhomeloans.feature.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.usecase.document.GetDocumentsUseCase
import com.mentorhomeloans.domain.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DocumentsViewModel managing document downloads and lists status.
 */
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentsUIState>(DocumentsUIState.Loading)
    val uiState: StateFlow<DocumentsUIState> = _uiState.asStateFlow()

    private val _downloadEvent = MutableSharedFlow<String>()
    val downloadEvent: SharedFlow<String> = _downloadEvent.asSharedFlow()

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            _uiState.value = DocumentsUIState.Loading
            getDocumentsUseCase("loan_99120").collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = DocumentsUIState.Success(result.data)
                    is Result.Error -> _uiState.value = DocumentsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = DocumentsUIState.Loading
                }
            }
        }
    }

    fun downloadDocument(document: Document) {
        viewModelScope.launch {
            when (val result = documentRepository.downloadDocument(document)) {
                is Result.Success -> _downloadEvent.emit("Document saved: ${result.data}")
                is Result.Error -> _downloadEvent.emit("Download failed: ${result.message}")
                else -> Unit
            }
        }
    }
}
