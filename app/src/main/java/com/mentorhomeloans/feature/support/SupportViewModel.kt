package com.mentorhomeloans.feature.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.SupportTicket
import com.mentorhomeloans.domain.model.TicketCategory
import com.mentorhomeloans.domain.model.TicketPriority
import com.mentorhomeloans.domain.model.TicketStatus
import com.mentorhomeloans.domain.repository.SupportRepository
import com.mentorhomeloans.domain.usecase.support.GetTicketsUseCase
import com.mentorhomeloans.domain.usecase.support.RaiseTicketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SupportViewModel managing support ticket state, admin config, filtering, document upload, and replies.
 */
@HiltViewModel
class SupportViewModel @Inject constructor(
    private val getTicketsUseCase: GetTicketsUseCase,
    private val raiseTicketUseCase: RaiseTicketUseCase,
    private val supportRepository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SupportUIState>(SupportUIState.Loading)
    val uiState: StateFlow<SupportUIState> = _uiState.asStateFlow()

    private val _ticketRaisedEvent = MutableSharedFlow<String>()
    val ticketRaisedEvent: SharedFlow<String> = _ticketRaisedEvent.asSharedFlow()

    private var currentAllTickets = emptyList<SupportTicket>()
    private var currentFilter = TicketFilterTab.ALL
    private var isAdminRaiseAllowed = true
    private var selectedTicket: SupportTicket? = null

    init {
        loadTickets()
    }

    fun loadTickets() {
        viewModelScope.launch {
            _uiState.value = SupportUIState.Loading
            getTicketsUseCase("CUST00123").collect { result ->
                when (result) {
                    is Result.Success -> {
                        currentAllTickets = result.data
                        updateSuccessState()
                    }
                    is Result.Error -> _uiState.value = SupportUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = SupportUIState.Loading
                }
            }
        }
    }

    fun setFilterTab(tab: TicketFilterTab) {
        currentFilter = tab
        updateSuccessState()
    }

    fun toggleAdminRaiseConfig() {
        isAdminRaiseAllowed = !isAdminRaiseAllowed
        updateSuccessState()
    }

    fun selectTicket(ticket: SupportTicket?) {
        selectedTicket = ticket
        updateSuccessState()
    }

    fun raiseTicket(
        category: TicketCategory,
        subject: String,
        description: String,
        attachmentUris: List<String> = emptyList(),
        priority: TicketPriority = TicketPriority.MEDIUM,
        loanAccountId: String? = null
    ) {
        if (!isAdminRaiseAllowed) {
            viewModelScope.launch {
                _ticketRaisedEvent.emit("Ticket creation is currently disabled by Admin.")
            }
            return
        }

        viewModelScope.launch {
            when (val result = raiseTicketUseCase(category, subject, description, attachmentUris, priority, loanAccountId)) {
                is Result.Success -> {
                    _ticketRaisedEvent.emit("Ticket ${result.data.ticketNumber} created successfully!")
                    loadTickets()
                }
                is Result.Error -> _ticketRaisedEvent.emit("Failed: ${result.message}")
                else -> Unit
            }
        }
    }

    fun sendReply(ticketId: String, messageText: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
            when (val result = supportRepository.replyToTicket(ticketId, messageText)) {
                is Result.Success -> {
                    _ticketRaisedEvent.emit("Reply sent!")
                    loadTickets()
                }
                is Result.Error -> _ticketRaisedEvent.emit("Failed to send reply: ${result.message}")
                else -> Unit
            }
        }
    }

    private fun updateSuccessState() {
        val filtered = when (currentFilter) {
            TicketFilterTab.ALL -> currentAllTickets
            TicketFilterTab.OPEN -> currentAllTickets.filter { it.status == TicketStatus.OPEN }
            TicketFilterTab.IN_PROGRESS -> currentAllTickets.filter { it.status == TicketStatus.IN_PROGRESS || it.status == TicketStatus.AWAITING_CUSTOMER }
            TicketFilterTab.RESOLVED -> currentAllTickets.filter { it.status == TicketStatus.RESOLVED || it.status == TicketStatus.CLOSED }
        }

        val updatedSelectedTicket = selectedTicket?.let { sel ->
            currentAllTickets.find { it.id == sel.id } ?: sel
        }

        _uiState.value = SupportUIState.Success(
            tickets = currentAllTickets,
            filteredTickets = filtered,
            selectedFilter = currentFilter,
            isAdminRaiseAllowed = isAdminRaiseAllowed,
            selectedTicket = updatedSelectedTicket
        )
    }
}
