package com.example.bckc.presentation.screens.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.domain.repository.UserRepository
import com.example.bckc.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    var verificationToken by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = MutableSharedFlow<SecurityUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var state by mutableStateOf(SecurityState())
        private set

    fun onEvent(event: SecurityEvent) {
        when (event) {
            is SecurityEvent.VerifyPassword -> {
                verifyPassword(event.currentPassword)
            }
            is SecurityEvent.ChangePassword -> {
                changePassword(event.newPassword, event.confirmPassword)
            }
            SecurityEvent.ResetState -> {
                state = SecurityState()
                verificationToken = null
                preferenceManager.clearVerificationToken()
            }
        }
    }

    private fun verifyPassword(currentPassword: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            userRepository.verifyPassword(currentPassword)
                .onSuccess { token ->
                    verificationToken = token
                    preferenceManager.saveVerificationToken(token)
                    _uiEvent.emit(SecurityUiEvent.NavigateToChangePassword)
                }
                .onFailure { e ->
                    _uiEvent.emit(SecurityUiEvent.ShowError(e.message ?: "Unknown error occurred"))
                }
            state = state.copy(isLoading = false)
        }
    }

    private fun changePassword(newPassword: String, confirmPassword: String) {
        if (newPassword != confirmPassword) {
            viewModelScope.launch {
                _uiEvent.emit(SecurityUiEvent.ShowError("Passwords do not match"))
            }
            return
        }

        if (!isPasswordValid(newPassword)) {
            viewModelScope.launch {
                _uiEvent.emit(SecurityUiEvent.ShowError("Password does not meet requirements"))
            }
            return
        }

        val token = preferenceManager.getVerificationToken()
        if (token == null) {
            viewModelScope.launch {
                _uiEvent.emit(SecurityUiEvent.ShowError("Verification token not found"))
            }
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            userRepository.changePassword(token, newPassword, confirmPassword)
                .onSuccess { message ->
                    preferenceManager.clearVerificationToken()
                    _uiEvent.emit(SecurityUiEvent.PasswordChanged(message))
                    _uiEvent.emit(SecurityUiEvent.NavigateToProfile)
                }
                .onFailure { e ->
                    _uiEvent.emit(SecurityUiEvent.ShowError(e.message ?: "Unknown error occurred"))
                }
            state = state.copy(isLoading = false)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { "!@#$".contains(it) }
    }
}

data class SecurityState(
    val isLoading: Boolean = false
)

sealed class SecurityEvent {
    data class VerifyPassword(val currentPassword: String) : SecurityEvent()
    data class ChangePassword(val newPassword: String, val confirmPassword: String) : SecurityEvent()
    object ResetState : SecurityEvent()
}

sealed class SecurityUiEvent {
    data class ShowError(val message: String) : SecurityUiEvent()
    data class PasswordChanged(val message: String) : SecurityUiEvent()
    object NavigateToChangePassword : SecurityUiEvent()
    object NavigateBack : SecurityUiEvent()
    object NavigateToProfile : SecurityUiEvent()
}
