package com.example.bckc.presentation.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.domain.model.User
import com.example.bckc.utils.Resource
import com.example.bckc.data.api.ApiService
import com.example.bckc.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    val tokenManager: TokenManager
) : ViewModel() {
    private val _profileState = MutableStateFlow<Resource<User>?>(null)
    val profileState: StateFlow<Resource<User>?> = _profileState

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading()
            try {
                val response = apiService.getUserProfile()
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        val user = User(
                            id = userResponse.id,
                            email = userResponse.email,
                            fullName = userResponse.full_name,
                            birthDate = userResponse.birth_date,
                            identityType = userResponse.identity_type,
                            institution = userResponse.institution,
                            profilePictureUrl = userResponse.profile_picture_url,
                            points = userResponse.points
                        )
                        _profileState.value = Resource.Success(user)
                    } ?: run {
                        _profileState.value = Resource.Error("Empty response body")
                    }
                } else {
                    _profileState.value = Resource.Error("Failed to fetch profile: ${response.message()}")
                }
            } catch (e: Exception) {
                _profileState.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }
}
