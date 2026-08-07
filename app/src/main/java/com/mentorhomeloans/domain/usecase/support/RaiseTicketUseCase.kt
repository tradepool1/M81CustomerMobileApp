package com.mentorhomeloans.domain.usecase.support

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.SupportTicket
import com.mentorhomeloans.domain.model.TicketCategory
import com.mentorhomeloans.domain.repository.SupportRepository
import javax.inject.Inject

import com.mentorhomeloans.domain.model.TicketPriority

/**
 * Use case to raise a new support ticket.
 *
 * @param supportRepository Repository handling support services.
 */
class RaiseTicketUseCase @Inject constructor(
    private val supportRepository: SupportRepository
) {
    /**
     * Raises a new ticket after validating requirements.
     *
     * @param category The ticket category.
     * @param subject Summary of the issue.
     * @param description Full explanation.
     * @param attachmentUris List of uploaded attachment uris.
     * @param priority Ticket priority level.
     * @param loanAccountId Associated loan account.
     * @return Result wrapping the created SupportTicket.
     */
    suspend operator fun invoke(
        category: TicketCategory,
        subject: String,
        description: String,
        attachmentUris: List<String> = emptyList(),
        priority: TicketPriority = TicketPriority.MEDIUM,
        loanAccountId: String? = null
    ): Result<SupportTicket> {
        if (subject.isBlank()) {
            return Result.Error(IllegalArgumentException("Subject cannot be empty"))
        }
        if (description.isBlank()) {
            return Result.Error(IllegalArgumentException("Description cannot be empty"))
        }
        return supportRepository.raiseTicket(category, subject, description, attachmentUris, priority, loanAccountId)
    }
}
