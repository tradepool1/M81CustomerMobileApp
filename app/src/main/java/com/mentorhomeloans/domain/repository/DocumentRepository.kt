package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Document
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for loan document operations.
 */
interface DocumentRepository {

    /**
     * Fetches master loan documents list from GetMasterRecords API.
     */
    fun getMasterDocuments(): Flow<Result<List<Document>>>

    /**
     * Sends request for a document via RequestDocument API.
     */
    suspend fun requestDocument(customerId: String, loanAcNo: String, documentTypeId: Int): Result<String>
}
