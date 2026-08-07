package com.mentorhomeloans.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.usecase.notification.GetNotificationsUseCase
import com.mentorhomeloans.domain.usecase.notification.MarkNotificationReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * NotificationsViewModel managing unread items and lists states.
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsUIState>(NotificationsUIState.Loading)
    val uiState: StateFlow<NotificationsUIState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsUIState.Loading
            getNotificationsUseCase("CUST00123").collect { result ->
                when (result) {
                    is Result.Success -> _uiState.value = NotificationsUIState.Success(result.data)
                    is Result.Error -> _uiState.value = NotificationsUIState.Error(result.message)
                    is Result.Loading -> _uiState.value = NotificationsUIState.Loading
                }
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            markNotificationReadUseCase(id)
            loadNotifications()
        }
    }
}
