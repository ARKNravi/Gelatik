package com.example.bckc.domain.repository

import com.example.bckc.utils.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<Boolean>
}
