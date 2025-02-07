package com.example.bckc.data.repository

import com.example.bckc.data.api.ApiService
import com.example.bckc.data.model.request.LoginRequest
import com.example.bckc.data.model.request.RegisterRequest
import com.example.bckc.domain.repository.AuthRepository
import com.example.bckc.utils.PreferenceManager
import com.example.bckc.utils.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Resource<String> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    Resource.Success(authResponse.access_token)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("Login failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun register(
        email: String,
        fullName: String,
        birthDate: String,
        identityType: String,
        password: String,
        passwordConfirm: String
    ): Resource<String> {
        return try {
            val request = RegisterRequest(
                email = email,
                full_name = fullName,
                birth_date = birthDate,
                identity_type = identityType,
                password = password,
                password_confirm = passwordConfirm
            )
            val response = apiService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    Resource.Success(authResponse.access_token)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("Registration failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
