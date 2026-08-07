package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Document
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for loan document operations.
 */
interface DocumentRepository {

    /**
     * Fetches the list of all available documents for a loan account.
     *
     * @param loanAccountId The loan account identifier.
     * @return A [Flow] of [Result]<List<[Document]>>.
     */
    fun getDocuments(loanAccountId: String): Flow<Result<List<Document>>>

    /**
     * Downloads a document to the device's downloads directory.
     *
     * @param document The [Document] to download.
     * @return [Result.Success] with the local file path on success.
     */
    suspend fun downloadDocument(document: Document): Result<String>
}
