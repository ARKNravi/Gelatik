package com.example.bckc.utils

import com.example.bckc.data.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) {
    private val _isTokenValid = MutableStateFlow<Boolean?>(null)
    val isTokenValid: StateFlow<Boolean?> = _isTokenValid

    suspend fun validateToken(): Boolean {
        val token = preferenceManager.getToken()
        
        // If no token exists, return false immediately
        if (token == null) {
            _isTokenValid.value = false
            return false
        }

        return try {
            // Try to get user profile with the stored token
            val response = apiService.getUserProfile()
            val isValid = response.isSuccessful
            
            if (!isValid) {
                // If token is invalid, clear it
                preferenceManager.clearToken()
            }
            
            _isTokenValid.value = isValid
            isValid
        } catch (e: Exception) {
            // If there's any error, clear token and return false
            preferenceManager.clearToken()
            _isTokenValid.value = false
            false
        }
    }

    fun clearToken() {
        preferenceManager.clearToken()
        _isTokenValid.value = false
    }
} 