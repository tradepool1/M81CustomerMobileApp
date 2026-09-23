package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.repository.DocumentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of DocumentRepository.
 */
@Singleton
class MockDocumentRepository @Inject constructor() : DocumentRepository {

    private val mockList = listOf(
        Document(id = 1, documentName = "Interest Certificate"),
        Document(id = 2, documentName = "LOD"),
        Document(id = 3, documentName = "Sanction Letter"),
        Document(id = 4, documentName = "SOA")
    )

    override fun getMasterDocuments(): Flow<Result<List<Document>>> = flow {
        emit(Result.Loading)
        delay(500)
        emit(Result.Success(mockList))
    }

    override suspend fun requestDocument(
        customerId: String,
        loanAcNo: String,
        documentTypeId: Int
    ): Result<String> {
        delay(800)
        return Result.Success("Document request submitted successfully.")
    }
}
