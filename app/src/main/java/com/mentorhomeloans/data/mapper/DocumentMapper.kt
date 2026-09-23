package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.remote.dto.MasterLoanDocumentDto
import com.mentorhomeloans.domain.model.Document

/**
 * Mapper for Document related data.
 */
object DocumentMapper {

    fun fromMasterDto(dto: MasterLoanDocumentDto): Document {
        return Document(
            id = dto.id,
            documentName = dto.documentName
        )
    }
}
