package com.example.bckc.data.repository

import com.example.bckc.data.api.ApiService
import com.example.bckc.data.model.request.LoginRequest
import com.example.bckc.domain.repository.AuthRepository
import com.example.bckc.utils.PreferenceManager
import com.example.bckc.utils.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Resource<Boolean> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    preferenceManager.saveToken(authResponse.access_token)
                    return Resource.Success(true)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("Login failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
