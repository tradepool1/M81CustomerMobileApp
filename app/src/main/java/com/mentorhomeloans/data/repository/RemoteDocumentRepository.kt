package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.api.DocumentApiService
import com.mentorhomeloans.data.remote.dto.RequestDocumentRequestDto
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote implementation of [DocumentRepository] calling real APIs.
 */
@Singleton
class RemoteDocumentRepository @Inject constructor(
    private val documentApiService: DocumentApiService
) : DocumentRepository {

    override fun getMasterDocuments(): Flow<Result<List<Document>>> = flow {
        emit(Result.Loading)
        try {
            val response = documentApiService.getMasterRecords()
            val list = response.loanDocuments?.map {
                Document(id = it.id, documentName = it.documentName)
            } ?: emptyList()
            emit(Result.Success(list))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun requestDocument(
        customerId: String,
        loanAcNo: String,
        documentTypeId: Int
    ): Result<String> {
        return try {
            val response = documentApiService.requestDocument(
                RequestDocumentRequestDto(
                    customerId = customerId,
                    loanAcNo = loanAcNo,
                    documentTypeId = documentTypeId
                )
            )
            if (response.status) {
                Result.Success(response.message ?: "Document request submitted successfully.")
            } else {
                Result.Error(Exception(response.message ?: "Failed to submit document request."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
