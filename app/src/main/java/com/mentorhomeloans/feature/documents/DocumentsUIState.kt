package com.mentorhomeloans.feature.documents

import com.mentorhomeloans.domain.model.Document

/**
 * UI State definition for loan documents list.
 */
sealed interface DocumentsUIState {
    object Loading : DocumentsUIState
    data class Success(val documents: List<Document>) : DocumentsUIState
    data class Error(val message: String) : DocumentsUIState
}
