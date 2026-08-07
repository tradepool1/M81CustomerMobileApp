package com.mentorhomeloans.feature.support

import com.mentorhomeloans.domain.model.SupportTicket

enum class TicketFilterTab(val label: String) {
    ALL("All Tickets"),
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}

/**
 * State parameters for support ticketing screen.
 */
sealed interface SupportUIState {
    object Loading : SupportUIState
    data class Success(
        val tickets: List<SupportTicket>,
        val filteredTickets: List<SupportTicket>,
        val selectedFilter: TicketFilterTab = TicketFilterTab.ALL,
        val isAdminRaiseAllowed: Boolean = true,
        val selectedTicket: SupportTicket? = null
    ) : SupportUIState
    data class Error(val message: String) : SupportUIState
}
