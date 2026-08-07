package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.model.DocumentType
import com.mentorhomeloans.domain.model.FileType
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
        Document(
            id = "d1",
            title = "Sanction Letter",
            description = "Official loan approval and term details sheet",
            type = DocumentType.SANCTION_LETTER,
            uploadedDate = "2024-07-28",
            fileUrl = "https://mentorhomeloans.com/docs/d1",
            fileType = FileType.PDF,
            sizeKb = 245,
            isPasswordProtected = false,
            thumbnailUrl = null
        ),
        Document(
            id = "d2",
            title = "Loan Agreement Copy",
            description = "Signed contract specifying home loan obligations",
            type = DocumentType.LOAN_AGREEMENT,
            uploadedDate = "2024-08-03",
            fileUrl = "https://mentorhomeloans.com/docs/d2",
            fileType = FileType.PDF,
            sizeKb = 1229, // ~1.2 MB
            isPasswordProtected = true,
            thumbnailUrl = null
        ),
        Document(
            id = "d3",
            title = "Aadhaar Card",
            description = "Primary applicant KYC verification file",
            type = DocumentType.KYC_DOCUMENT,
            uploadedDate = "2024-07-20",
            fileUrl = "https://mentorhomeloans.com/docs/d3",
            fileType = FileType.PDF,
            sizeKb = 512,
            isPasswordProtected = false,
            thumbnailUrl = null
        )
    )

    override fun getDocuments(loanAccountId: String): Flow<Result<List<Document>>> = flow {
        emit(Result.Loading)
        delay(800)
        emit(Result.Success(mockList))
    }

    override suspend fun downloadDocument(document: Document): Result<String> {
        delay(1500)
        return Result.Success("/storage/emulated/0/Download/${document.title.replace(" ", "_")}.pdf")
    }
}
