package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.mapper.DocumentMapper
import com.mentorhomeloans.data.remote.api.DocumentApiService
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

    override fun getDocuments(loanAccountId: String): Flow<Result<List<Document>>> = flow {
        emit(Result.Loading)
        try {
            val response = documentApiService.getDocuments(loanAccountId)
            if (response.success && response.data != null) {
                val domainList = response.data.map { DocumentMapper.fromDto(it) }
                emit(Result.Success(domainList))
            } else {
                emit(Result.Error(Exception(response.message ?: "Failed to fetch documents")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun downloadDocument(document: Document): Result<String> {
        // This is a placeholder for actual file download logic if needed.
        // For now, we return a mock path as requested by the UI flow.
        return Result.Success("/storage/emulated/0/Download/${document.title.replace(" ", "_")}.pdf")
    }

    override suspend fun getLoanDocuments(loanAcNo: String): Result<String> {
        return try {
            val response = documentApiService.getLoanDocuments(loanAcNo)
            if (response.status) {
                Result.Success(response.message ?: "Success")
            } else {
                Result.Error(Exception(response.message ?: "Failed to trigger download"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
