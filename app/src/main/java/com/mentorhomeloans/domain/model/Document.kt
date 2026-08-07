package com.mentorhomeloans.domain.model

/**
 * Represents a loan-related legal or operational document.
 *
 * Documents include sanction letters, loan agreements, KYC documents,
 * and disbursement details. Available for viewing and download from
 * the Loan Documents screen.
 *
 * @property id             Unique document identifier.
 * @property title          Display title of the document.
 * @property description    Brief description of the document's purpose.
 * @property type           Category/type of document.
 * @property uploadedDate   Date the document was generated/uploaded (yyyy-MM-dd).
 * @property fileUrl        Secure URL to download or view the document.
 * @property fileType       File format (PDF, JPEG, PNG).
 * @property sizeKb         File size in kilobytes.
 * @property isPasswordProtected True if the document requires a password to open.
 * @property isDownloaded   True if the file has been saved to the device.
 * @property thumbnailUrl   Optional thumbnail image URL for preview.
 */
data class Document(
    val id: String,
    val title: String,
    val description: String,
    val type: DocumentType,
    val uploadedDate: String,
    val fileUrl: String,
    val fileType: FileType,
    val sizeKb: Int,
    val isPasswordProtected: Boolean,
    val isDownloaded: Boolean = false,
    val thumbnailUrl: String?
)

/**
 * Categories of loan documents.
 *
 * @property displayName Human-readable category name.
 * @property iconDescription Brief description of the icon to use (for accessibility).
 */
enum class DocumentType(val displayName: String) {
    SANCTION_LETTER("Sanction Letter"),
    LOAN_AGREEMENT("Loan Agreement"),
    KYC_DOCUMENT("KYC Document"),
    DISBURSEMENT_LETTER("Disbursement Letter"),
    INSURANCE_POLICY("Insurance Policy"),
    NOC("No Objection Certificate"),
    LEGAL_DOCUMENT("Legal Document"),
    OTHER("Other")
}

/**
 * File format type.
 *
 * @property mimeType MIME type string for intent/download handling.
 * @property extension File extension.
 */
enum class FileType(val mimeType: String, val extension: String) {
    PDF("application/pdf", ".pdf"),
    JPEG("image/jpeg", ".jpg"),
    PNG("image/png", ".png")
}
