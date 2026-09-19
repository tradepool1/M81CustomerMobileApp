package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.remote.dto.DocumentDto
import com.mentorhomeloans.domain.model.Document
import com.mentorhomeloans.domain.model.DocumentType
import com.mentorhomeloans.domain.model.FileType

/**
 * Mapper for Document related data.
 */
object DocumentMapper {

    fun fromDto(dto: DocumentDto): Document {
        return Document(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            type = mapType(dto.type),
            uploadedDate = dto.uploadedDate,
            fileUrl = dto.fileUrl,
            fileType = mapFileType(dto.fileType),
            sizeKb = dto.sizeKb,
            isPasswordProtected = dto.isPasswordProtected,
            thumbnailUrl = dto.thumbnailUrl
        )
    }

    private fun mapType(type: String): DocumentType {
        return try {
            DocumentType.valueOf(type.uppercase())
        } catch (e: Exception) {
            DocumentType.OTHER
        }
    }

    private fun mapFileType(fileType: String): FileType {
        return when (fileType.lowercase()) {
            "pdf" -> FileType.PDF
            "jpeg", "jpg" -> FileType.JPEG
            "png" -> FileType.PNG
            else -> FileType.PDF
        }
    }
}
