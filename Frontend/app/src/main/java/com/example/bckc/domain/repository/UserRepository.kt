package com.example.bckc.domain.repository

interface UserRepository {
    suspend fun verifyPassword(currentPassword: String): Result<String>
    suspend fun changePassword(verificationToken: String, newPassword: String, newPasswordConfirm: String): Result<String>
}