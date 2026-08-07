package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.SupportTicket
import com.mentorhomeloans.domain.model.TicketCategory
import com.mentorhomeloans.domain.model.TicketPriority
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for customer support ticket operations.
 */
interface SupportRepository {

    /**
     * Observes all support tickets for the logged-in customer.
     *
     * @param customerId The customer identifier.
     * @return A [Flow] of [Result]<List<[SupportTicket]>>.
     */
    fun getTickets(customerId: String): Flow<Result<List<SupportTicket>>>

    /**
     * Fetches a single ticket by its unique ID.
     *
     * @param ticketId The unique ticket identifier.
     * @return [Result.Success] with the [SupportTicket], [Result.Error] if not found.
     */
    suspend fun getTicketById(ticketId: String): Result<SupportTicket>

    /**
     * Raises a new support ticket.
     *
     * @param category        The issue category.
     * @param subject         One-line summary.
     * @param description     Detailed description.
     * @param attachmentUris  List of local file URIs to upload as attachments.
     * @param priority        Ticket priority.
     * @param loanAccountId   Optional associated loan account.
     * @return [Result.Success] with the created [SupportTicket].
     */
    suspend fun raiseTicket(
        category: TicketCategory,
        subject: String,
        description: String,
        attachmentUris: List<String>,
        priority: TicketPriority = TicketPriority.MEDIUM,
        loanAccountId: String? = null
    ): Result<SupportTicket>

    /**
     * Adds a reply message to an existing ticket thread.
     *
     * @param ticketId The ticket to reply to.
     * @param message  The reply message text.
     * @return [Result.Success] with Unit on success.
     */
    suspend fun replyToTicket(ticketId: String, message: String): Result<Unit>
}
