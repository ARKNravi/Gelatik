package com.example.bckc.data.repository

import com.example.bckc.data.api.ApiService
import com.example.bckc.data.model.request.ChangePasswordRequest
import com.example.bckc.data.model.request.VerifyPasswordRequest
import com.example.bckc.data.model.response.UserResponse
import com.example.bckc.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override suspend fun verifyPassword(currentPassword: String): Result<String> {
        return try {
            val response = apiService.verifyPassword(VerifyPasswordRequest(currentPassword))
            Result.success(response.verification_token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        verificationToken: String,
        newPassword: String,
        newPasswordConfirm: String
    ): Result<String> {
        return try {
            val response = apiService.changePassword(
                ChangePasswordRequest(
                    verification_token = verificationToken,
                    new_password = newPassword,
                    new_password_confirm = newPasswordConfirm
                )
            )
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): UserResponse {
        val response = apiService.getUserProfile()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Profile data is null")
        } else {
            throw Exception("Failed to get profile: ${response.message()}")
        }
    }
}