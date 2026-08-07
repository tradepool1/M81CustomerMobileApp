package com.mentorhomeloans.domain.usecase.support

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.SupportTicket
import com.mentorhomeloans.domain.repository.SupportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all support tickets for a customer.
 *
 * @param supportRepository Repository handling support services.
 */
class GetTicketsUseCase @Inject constructor(
    private val supportRepository: SupportRepository
) {
    /**
     * Executes the ticket list retrieval.
     *
     * @param customerId The customer identifier.
     * @return Flow of ticket lists wrapped in Result.
     */
    operator fun invoke(customerId: String): Flow<Result<List<SupportTicket>>> {
        return supportRepository.getTickets(customerId)
    }
}
