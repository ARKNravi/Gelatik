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
        if (token == null) {
            _isTokenValid.value = false
            return false
        }

        return try {
            val response = apiService.getUserProfile()
            val isValid = response.isSuccessful
            _isTokenValid.value = isValid
            if (!isValid) {
                preferenceManager.clearToken()
            }
            isValid
        } catch (e: Exception) {
            _isTokenValid.value = false
            preferenceManager.clearToken()
            false
        }
    }

    fun clearToken() {
        preferenceManager.clearToken()
        _isTokenValid.value = false
    }
} 