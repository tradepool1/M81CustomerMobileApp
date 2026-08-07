package com.mentorhomeloans.domain.model

/**
 * Represents a loan account statement record.
 *
 * Statements are periodic summaries (monthly, quarterly, annual) of account activity.
 * Users can view and download these as PDF or CSV from the Statements screen.
 *
 * @property id             Unique statement identifier.
 * @property period         Human-readable period description (e.g. "July 2024").
 * @property fromDate       Start date of the statement period (yyyy-MM-dd).
 * @property toDate         End date of the statement period (yyyy-MM-dd).
 * @property generatedDate  Date the statement was generated (yyyy-MM-dd).
 * @property openingBalance Principal balance at the start of the period.
 * @property closingBalance Principal balance at the end of the period.
 * @property totalPaid      Total amount paid during the period.
 * @property type           Statement type (Monthly / Annual / Custom).
 * @property pdfUrl         URL to download the PDF version.
 * @property csvUrl         URL to download the CSV version.
 * @property sizeKb         Approximate file size in kilobytes.
 * @property isDownloaded   True if the PDF has been saved locally.
 */
data class Statement(
    val id: String,
    val period: String,
    val fromDate: String,
    val toDate: String,
    val generatedDate: String,
    val openingBalance: Double,
    val closingBalance: Double,
    val totalPaid: Double,
    val type: StatementType,
    val pdfUrl: String,
    val csvUrl: String,
    val sizeKb: Int,
    val isDownloaded: Boolean = false
)

/**
 * Classification of statement period types.
 *
 * @property displayName Human-readable label.
 */
enum class StatementType(val displayName: String) {
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    ANNUAL("Annual"),
    CUSTOM("Custom Range")
}
