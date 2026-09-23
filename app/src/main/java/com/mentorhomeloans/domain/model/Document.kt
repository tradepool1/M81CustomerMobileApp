package com.mentorhomeloans.domain.model

/**
 * Represents a loan document item returned from master records and available for request.
 *
 * @property id           Numeric type ID of the document (e.g. 1, 2, 3...).
 * @property documentName Title/name of the document (e.g. "Interest Certificate", "LOD", "Sanction Letter", "SOA").
 */
data class Document(
    val id: Int,
    val documentName: String
)
