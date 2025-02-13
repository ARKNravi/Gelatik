package com.example.bckc.presentation.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.data.model.response.UserResponse
import com.example.bckc.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserResponse?>(null)
    val userProfile: StateFlow<UserResponse?> = _userProfile.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val response = userRepository.getProfile()
                _userProfile.value = response
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}