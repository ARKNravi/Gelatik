package com.example.bckc.domain.usecase.auth

import com.example.bckc.domain.repository.AuthRepository
import com.example.bckc.utils.Resource
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        fullName: String,
        birthDate: String,
        identityType: String,
        password: String,
        passwordConfirm: String
    ): Resource<String> {
        // Basic validation to ensure no empty fields
        if (email.isBlank() || fullName.isBlank() || birthDate.isBlank() || 
            identityType.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
            return Resource.Error("All fields must be filled")
        }

        // Call repository to make the API request
        return repository.register(email, fullName, birthDate, identityType, password, passwordConfirm)
    }
}
