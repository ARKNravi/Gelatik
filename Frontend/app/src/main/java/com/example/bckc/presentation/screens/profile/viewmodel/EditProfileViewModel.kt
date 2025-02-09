package com.example.bckc.presentation.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.data.api.ApiService
import com.example.bckc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class IdentityType {
    TULI, DENGAR
}

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val birthDate: Date? = null,
    val identityType: IdentityType = IdentityType.TULI,
    val institution: String = "",
    val profilePictureUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFormValid: Boolean = false,
    val updateSuccess: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getUserProfile()
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val birthDate = userResponse.birth_date?.let { dateFormat.parse(it) }

                        _uiState.update { state ->
                            state.copy(
                                fullName = userResponse.full_name,
                                email = userResponse.email,
                                birthDate = birthDate,
                                identityType = if (userResponse.identity_type == "tuli") IdentityType.TULI else IdentityType.DENGAR,
                                institution = userResponse.institution ?: "",
                                profilePictureUrl = userResponse.profile_picture_url,
                                isLoading = false,
                                isFormValid = true
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(error = "Failed to load profile", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun updateFullName(value: String) {
        _uiState.update {
            it.copy(
                fullName = value,
                isFormValid = value.isNotBlank() && it.birthDate != null
            )
        }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updateBirthDate(date: Date) {
        _uiState.update {
            it.copy(
                birthDate = date,
                isFormValid = it.fullName.isNotBlank() && date != null
            )
        }
    }

    fun updateIdentityType(type: IdentityType) {
        _uiState.update { it.copy(identityType = type) }
    }

    fun updateInstitution(value: String) {
        _uiState.update { it.copy(institution = value) }
    }

    fun updateProfilePicture(url: String) {
        _uiState.update { it.copy(profilePictureUrl = url) }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = _uiState.value.birthDate?.let { dateFormat.format(it) }

                val requestBody = mutableMapOf<String, String>(
                    "full_name" to _uiState.value.fullName,
                    "birth_date" to (formattedDate ?: ""),
                    "institution" to _uiState.value.institution
                )

                _uiState.value.profilePictureUrl?.let { requestBody["profile_picture_url"] = it }

                val response = apiService.updateUserProfile(requestBody)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = null,
                        updateSuccess = true
                    )}
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to update profile",
                        updateSuccess = false
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message,
                    updateSuccess = false
                )}
            }
        }
    }
}
