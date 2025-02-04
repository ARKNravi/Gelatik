package com.example.bckc.domain.usecase.auth

import com.example.bckc.domain.repository.AuthRepository
import com.example.bckc.utils.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<Boolean> {
        if (email.isBlank() || password.isBlank()) {
            return Resource.Error("Email and password cannot be empty")
        }
        return repository.login(email, password)
    }
}
