package com.example.bckc.domain.repository

import com.example.bckc.utils.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<Boolean>
    suspend fun register(
        email: String,
        fullName: String,
        birthDate: String,
        identityType: String,
        password: String,
        passwordConfirm: String
    ): Resource<Boolean>
}
