package com.mentorhomeloans.domain.model

/**
 * Represents a customer support ticket.
 *
 * Support tickets allow customers to raise issues, track resolution progress,
 * and maintain a complete communication history. Used in the Support screen.
 *
 * @property id              Unique ticket identifier.
 * @property ticketNumber    Human-readable ticket number (e.g. "TKT-2024-00123").
 * @property category        Category of the support issue.
 * @property subject         Brief one-line summary of the issue.
 * @property description     Detailed description of the problem.
 * @property status          Current resolution status.
 * @property priority        Ticket priority level.
 * @property createdDate     Date and time ticket was created (ISO-8601).
 * @property updatedDate     Date and time of last update (ISO-8601).
 * @property resolvedDate    Date ticket was resolved (null if unresolved).
 * @property assignedTo      Name of the support agent handling the ticket.
 * @property attachments     List of uploaded document/image attachments.
 * @property messages        Conversation thread between customer and support.
 */
data class SupportTicket(
    val id: String,
    val ticketNumber: String,
    val category: TicketCategory,
    val subject: String,
    val description: String,
    val status: TicketStatus,
    val priority: TicketPriority,
    val createdDate: String,
    val updatedDate: String,
    val resolvedDate: String?,
    val assignedTo: String?,
    val attachments: List<TicketAttachment>,
    val messages: List<TicketMessage>,
    val slaHours: Int = 24,
    val targetResolutionDate: String = "",
    val slaStatus: SLAStatus = SLAStatus.ON_TRACK,
    val loanAccountId: String? = null
)

/**
 * SLA Resolution Tracking Status
 */
enum class SLAStatus(val displayName: String) {
    ON_TRACK("On Track"),
    BREACH_RISK("Breach Risk"),
    BREACHED("SLA Breached"),
    RESOLVED_ON_TIME("Resolved On Time")
}

/**
 * Categories available when raising a support ticket.
 *
 * @property displayName Human-readable category name.
 */
enum class TicketCategory(val displayName: String) {
    STATEMENT_ISSUE("Statement Issue"),
    PAYMENT_ISSUE("Payment Issue"),
    LOAN_QUERY("Loan Query"),
    TECHNICAL_ISSUE("Technical Issue"),
    CONTACT_ADDRESS_CHANGE("Change in contact details/address"),
    GENERAL_INQUIRY("General Inquiry"),
    OTHER("Other")
}

/**
 * Current resolution status of a support ticket.
 *
 * @property displayName Human-readable status.
 */
enum class TicketStatus(val displayName: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    AWAITING_CUSTOMER("Awaiting Customer Response"),
    RESOLVED("Resolved"),
    CLOSED("Closed"),
    REOPENED("Reopened")
}

/**
 * Priority level of a support ticket.
 *
 * @property displayName Human-readable priority label.
 */
enum class TicketPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical")
}

/**
 * A file attached to a support ticket by the customer or agent.
 *
 * @property id       Attachment identifier.
 * @property fileName File name.
 * @property fileUrl  URL to download the attachment.
 * @property sizeKb   File size in kilobytes.
 * @property uploadedBy Who uploaded this attachment ("Customer" or "Support").
 */
data class TicketAttachment(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val sizeKb: Int,
    val uploadedBy: String
)

/**
 * A single message in the support ticket conversation thread.
 *
 * @property id         Message identifier.
 * @property message    Message body text.
 * @property sentBy     Author of the message ("Customer" or "Support").
 * @property sentAt     Timestamp of the message (ISO-8601).
 * @property isRead     True if the message has been read by the recipient.
 */
data class TicketMessage(
    val id: String,
    val message: String,
    val sentBy: String,
    val sentAt: String,
    val isRead: Boolean
)
