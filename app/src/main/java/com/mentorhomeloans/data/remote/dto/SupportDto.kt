package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Message schema properties.
 */
data class TicketMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("message") val message: String,
    @SerializedName("sentBy") val sentBy: String,
    @SerializedName("sentAt") val sentAt: String,
    @SerializedName("isRead") val isRead: Boolean
)

/**
 * Attachment details schema.
 */
data class TicketAttachmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("fileName") val fileName: String,
    @SerializedName("fileUrl") val fileUrl: String,
    @SerializedName("sizeKb") val sizeKb: Int,
    @SerializedName("uploadedBy") val uploadedBy: String
)

/**
 * Main Ticket data schema properties.
 */
data class SupportTicketDto(
    @SerializedName("id") val id: String,
    @SerializedName("ticketNumber") val ticketNumber: String,
    @SerializedName("category") val category: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("description") val description: String,
    @SerializedName("status") val status: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("updatedDate") val updatedDate: String,
    @SerializedName("resolvedDate") val resolvedDate: String?,
    @SerializedName("assignedTo") val assignedTo: String?,
    @SerializedName("attachments") val attachments: List<TicketAttachmentDto>,
    @SerializedName("messages") val messages: List<TicketMessageDto>
)
