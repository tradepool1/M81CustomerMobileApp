package com.mentorhomeloans.feature.profile

import com.mentorhomeloans.domain.model.User

/**
 * State parameters for Profile view.
 */
sealed interface ProfileUIState {
    object Loading : ProfileUIState
    data class Success(val user: User) : ProfileUIState
    data class Error(val message: String) : ProfileUIState
}
