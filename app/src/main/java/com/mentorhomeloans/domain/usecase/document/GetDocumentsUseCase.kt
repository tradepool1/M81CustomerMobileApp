package com.mentorhomeloans.domain.usecase.document

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching loan documents.
 *
 * @param documentRepository Data source for document records.
 */
class GetDocumentsUseCase @Inject constructor(
    private val documentRepository: DocumentRepository
) {
    /**
     * Returns a [Flow] of all loan documents for the given account.
     *
     * @param loanAccountId The loan account identifier.
     * @return [Flow]<[Result]<List<[Document]>>>.
     */
    operator fun invoke(loanAccountId: String): Flow<Result<List<Document>>> {
        return documentRepository.getDocuments(loanAccountId)
    }
}
