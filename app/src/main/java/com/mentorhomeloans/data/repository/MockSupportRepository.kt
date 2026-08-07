package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.SLAStatus
import com.mentorhomeloans.domain.model.SupportTicket
import com.mentorhomeloans.domain.model.TicketAttachment
import com.mentorhomeloans.domain.model.TicketCategory
import com.mentorhomeloans.domain.model.TicketMessage
import com.mentorhomeloans.domain.model.TicketPriority
import com.mentorhomeloans.domain.model.TicketStatus
import com.mentorhomeloans.domain.repository.SupportRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of SupportRepository with SLA tracking and file attachments.
 */
@Singleton
class MockSupportRepository @Inject constructor() : SupportRepository {

    private val mockTickets = mutableListOf(
        SupportTicket(
            id = "tkt_1",
            ticketNumber = "TKT-2026-04981",
            category = TicketCategory.PAYMENT_ISSUE,
            subject = "Double deduction for July EMI",
            description = "My account was auto-debited twice for the EMI on 5th July. Kindly refund the duplicate amount of ₹28,500.",
            status = TicketStatus.IN_PROGRESS,
            priority = TicketPriority.HIGH,
            createdDate = "2026-07-20T10:15:30Z",
            updatedDate = "2026-07-21T09:00:00Z",
            resolvedDate = null,
            assignedTo = "Vikram Aditya (Accounts Manager)",
            attachments = listOf(
                TicketAttachment("a1", "bank_statement_july.pdf", "https://mentorhomeloans.com/attachments/a1", 240, "Customer"),
                TicketAttachment("a2", "upi_receipt.png", "https://mentorhomeloans.com/attachments/a2", 120, "Customer")
            ),
            messages = listOf(
                TicketMessage("m1", "Ticket registered. Assigned to Banking Operations team.", "System", "2026-07-20T10:15:30Z", true),
                TicketMessage("m2", "We have initiated a transaction search with HDFC Bank. Refund will be processed within 24 working hours.", "Support", "2026-07-21T09:00:00Z", false)
            ),
            slaHours = 24,
            targetResolutionDate = "2026-07-21T10:15:30Z",
            slaStatus = SLAStatus.BREACH_RISK,
            loanAccountId = "MHL-2024-88492"
        ),
        SupportTicket(
            id = "tkt_2",
            ticketNumber = "TKT-2026-04102",
            category = TicketCategory.STATEMENT_ISSUE,
            subject = "Annual Interest Certificate (IT Certificate) missing FY 2025-26",
            description = "I require the Provisional Interest Certificate for my home loan for tax declaration purposes.",
            status = TicketStatus.OPEN,
            priority = TicketPriority.MEDIUM,
            createdDate = "2026-07-21T08:30:00Z",
            updatedDate = "2026-07-21T08:30:00Z",
            resolvedDate = null,
            assignedTo = "Priya Sharma (Customer Desk)",
            attachments = emptyList(),
            messages = listOf(
                TicketMessage("m1", "Your request is queued for automated generation.", "System", "2026-07-21T08:30:00Z", true)
            ),
            slaHours = 12,
            targetResolutionDate = "2026-07-21T20:30:00Z",
            slaStatus = SLAStatus.ON_TRACK,
            loanAccountId = "MHL-2024-88492"
        ),
        SupportTicket(
            id = "tkt_3",
            ticketNumber = "TKT-2026-03889",
            category = TicketCategory.CONTACT_ADDRESS_CHANGE,
            subject = "Updated mobile number and communication address",
            description = "Requesting change of registered mobile number to +91 98765 43210 along with proof of new address.",
            status = TicketStatus.RESOLVED,
            priority = TicketPriority.LOW,
            createdDate = "2026-07-15T11:20:00Z",
            updatedDate = "2026-07-16T14:45:00Z",
            resolvedDate = "2026-07-16T14:45:00Z",
            assignedTo = "Amit Kumar (KYC Team)",
            attachments = listOf(
                TicketAttachment("a3", "aadhaar_card_updated.pdf", "https://mentorhomeloans.com/attachments/a3", 512, "Customer")
            ),
            messages = listOf(
                TicketMessage("m1", "Address proof validated successfully. Address and phone updated in CBS system.", "Support", "2026-07-16T14:45:00Z", true)
            ),
            slaHours = 48,
            targetResolutionDate = "2026-07-17T11:20:00Z",
            slaStatus = SLAStatus.RESOLVED_ON_TIME,
            loanAccountId = "MHL-2024-88492"
        ),
        SupportTicket(
            id = "tkt_4",
            ticketNumber = "TKT-2026-02910",
            category = TicketCategory.LOAN_QUERY,
            subject = "Query regarding Home Loan Part-Prepayment eligibility",
            description = "Can I make a partial prepayment of ₹2,000,000 without penalty? What is the maximum number of prepayments permitted per fiscal year?",
            status = TicketStatus.RESOLVED,
            priority = TicketPriority.MEDIUM,
            createdDate = "2026-07-02T16:00:00Z",
            updatedDate = "2026-07-03T10:30:00Z",
            resolvedDate = "2026-07-03T10:30:00Z",
            assignedTo = "Rohan Verma",
            attachments = emptyList(),
            messages = listOf(
                TicketMessage("m1", "Floating interest rate loans have zero prepayment charges as per RBI rules. You can initiate prepayment directly via the Repayment tab in MentorApp.", "Support", "2026-07-03T10:30:00Z", true)
            ),
            slaHours = 24,
            targetResolutionDate = "2026-07-03T16:00:00Z",
            slaStatus = SLAStatus.RESOLVED_ON_TIME,
            loanAccountId = "MHL-2024-88492"
        )
    )

    override fun getTickets(customerId: String): Flow<Result<List<SupportTicket>>> = flow {
        emit(Result.Loading)
        delay(400)
        emit(Result.Success(mockTickets))
    }

    override suspend fun getTicketById(ticketId: String): Result<SupportTicket> {
        delay(300)
        val ticket = mockTickets.find { it.id == ticketId }
        return if (ticket != null) Result.Success(ticket) else Result.Error(Exception("Ticket not found"))
    }

    override suspend fun raiseTicket(
        category: TicketCategory,
        subject: String,
        description: String,
        attachmentUris: List<String>,
        priority: TicketPriority,
        loanAccountId: String?
    ): Result<SupportTicket> {
        delay(600)
        val slaHoursForCategory = when (category) {
            TicketCategory.STATEMENT_ISSUE -> 12
            TicketCategory.PAYMENT_ISSUE -> 24
            TicketCategory.LOAN_QUERY -> 24
            TicketCategory.TECHNICAL_ISSUE -> 12
            TicketCategory.CONTACT_ADDRESS_CHANGE -> 48
            else -> 24
        }
        
        val newTicket = SupportTicket(
            id = UUID.randomUUID().toString(),
            ticketNumber = "TKT-2026-0${(10000..99999).random()}",
            category = category,
            subject = subject,
            description = description,
            status = TicketStatus.OPEN,
            priority = priority,
            createdDate = "2026-07-21T15:00:00Z",
            updatedDate = "2026-07-21T15:00:00Z",
            resolvedDate = null,
            assignedTo = "Auto-Assigned Desk",
            attachments = attachmentUris.mapIndexed { i, uri ->
                val fileName = uri.substringAfterLast("/").ifEmpty { "attachment_${i + 1}.pdf" }
                TicketAttachment("att_$i", fileName, uri, (150..650).random(), "Customer")
            },
            messages = listOf(
                TicketMessage("m0", "Ticket created successfully. Our team will review it shortly.", "System", "2026-07-21T15:00:00Z", true)
            ),
            slaHours = slaHoursForCategory,
            targetResolutionDate = "2026-07-22T15:00:00Z",
            slaStatus = SLAStatus.ON_TRACK,
            loanAccountId = loanAccountId ?: "MHL-2024-88492"
        )
        mockTickets.add(0, newTicket)
        return Result.Success(newTicket)
    }

    override suspend fun replyToTicket(ticketId: String, message: String): Result<Unit> {
        delay(400)
        val index = mockTickets.indexOfFirst { it.id == ticketId }
        if (index != -1) {
            val oldTicket = mockTickets[index]
            val newMsg = TicketMessage(
                id = UUID.randomUUID().toString(),
                message = message,
                sentBy = "Customer",
                sentAt = "2026-07-21T15:05:00Z",
                isRead = true
            )
            mockTickets[index] = oldTicket.copy(
                messages = oldTicket.messages + newMsg,
                updatedDate = "2026-07-21T15:05:00Z"
            )
            return Result.Success(Unit)
        }
        return Result.Error(Exception("Ticket not found"))
    }
}
