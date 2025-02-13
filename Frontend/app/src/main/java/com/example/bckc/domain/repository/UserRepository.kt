package com.example.bckc.domain.repository

import com.example.bckc.data.model.response.UserResponse

interface UserRepository {
    suspend fun verifyPassword(currentPassword: String): Result<String>
    suspend fun changePassword(verificationToken: String, newPassword: String, newPasswordConfirm: String): Result<String>
    suspend fun getProfile(): UserResponse
}