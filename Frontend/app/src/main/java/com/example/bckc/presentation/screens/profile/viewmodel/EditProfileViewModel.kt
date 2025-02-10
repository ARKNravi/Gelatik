package com.example.bckc.presentation.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.data.api.ApiService
import com.example.bckc.utils.Resource
import com.example.bckc.utils.PreferenceManager
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
    val updateSuccess: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val initialState: Map<String, Any?> = mapOf()
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getUserProfile()
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val birthDate = userResponse.birth_date?.let { dateFormat.parse(it) }

                        // Save initial data to preferences
                        preferenceManager.saveProfileData(
                            fullName = userResponse.full_name,
                            birthDate = userResponse.birth_date ?: "",
                            identityType = userResponse.identity_type,
                            institution = userResponse.institution ?: "",
                            profilePictureUrl = userResponse.profile_picture_url
                        )

                        _uiState.update { state ->
                            state.copy(
                                fullName = userResponse.full_name,
                                email = userResponse.email,
                                birthDate = birthDate,
                                identityType = if (userResponse.identity_type == "tuli") IdentityType.TULI else IdentityType.DENGAR,
                                institution = userResponse.institution ?: "",
                                profilePictureUrl = userResponse.profile_picture_url,
                                isLoading = false,
                                isFormValid = true,
                                hasUnsavedChanges = false
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

    private fun checkForChanges(currentState: EditProfileUiState): Boolean {
        val savedData = preferenceManager.getProfileData()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentBirthDate = currentState.birthDate?.let { dateFormat.format(it) } ?: ""
        
        println("Checking changes:")
        println("Saved fullName: ${savedData["fullName"]}")
        println("Current fullName: ${currentState.fullName}")
        println("Saved birthDate: ${savedData["birthDate"]}")
        println("Current birthDate: $currentBirthDate")
        println("Saved identityType: ${savedData["identityType"]}")
        println("Current identityType: ${currentState.identityType.name.lowercase()}")
        println("Saved institution: ${savedData["institution"]}")
        println("Current institution: ${currentState.institution}")
        
        val hasChanges = currentState.fullName != savedData["fullName"] ||
               currentBirthDate != savedData["birthDate"] ||
               currentState.identityType.name.lowercase() != savedData["identityType"] ||
               currentState.institution != savedData["institution"] ||
               currentState.profilePictureUrl != savedData["profilePictureUrl"]
               
        println("Has changes: $hasChanges")
        return hasChanges
    }

    fun updateFullName(value: String) {
        _uiState.update {
            val newState = it.copy(
                fullName = value,
                isFormValid = value.isNotBlank() && it.birthDate != null
            )
            newState.copy(hasUnsavedChanges = checkForChanges(newState))
        }
    }

    fun updateEmail(value: String) {
        _uiState.update { 
            val newState = it.copy(email = value)
            newState.copy(hasUnsavedChanges = checkForChanges(newState))
        }
    }

    fun updateBirthDate(date: Date) {
        _uiState.update {
            val newState = it.copy(
                birthDate = date,
                isFormValid = it.fullName.isNotBlank() && date != null
            )
            newState.copy(hasUnsavedChanges = checkForChanges(newState))
        }
    }

    fun updateIdentityType(type: IdentityType) {
        _uiState.update { 
            val newState = it.copy(identityType = type)
            newState.copy(hasUnsavedChanges = checkForChanges(newState))
        }
    }

    fun updateInstitution(value: String) {
        _uiState.update { 
            val newState = it.copy(institution = value)
            newState.copy(hasUnsavedChanges = checkForChanges(newState))
        }
    }

    fun updateProfilePicture(url: String) {
        _uiState.update { 
            val newState = it.copy(profilePictureUrl = url)
            newState.copy(hasUnsavedChanges = checkForChanges(newState))
        }
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
                    // Update saved profile data after successful update
                    preferenceManager.saveProfileData(
                        fullName = _uiState.value.fullName,
                        birthDate = formattedDate ?: "",
                        identityType = _uiState.value.identityType.name.lowercase(),
                        institution = _uiState.value.institution,
                        profilePictureUrl = _uiState.value.profilePictureUrl
                    )
                    
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = null,
                        updateSuccess = true,
                        hasUnsavedChanges = false
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

    override fun onCleared() {
        super.onCleared()
        preferenceManager.clearProfileData()
    }
}
