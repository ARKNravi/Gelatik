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
    ): Resource<Boolean> {
        if (email.isBlank() || fullName.isBlank() || birthDate.isBlank() || 
            identityType.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
            return Resource.Error("All fields must be filled")
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            return Resource.Error("Invalid email format")
        }

        if (password != passwordConfirm) {
            return Resource.Error("Passwords do not match")
        }

        // Password validation
        val hasMinLength = password.length >= 8
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        if (!hasMinLength || !hasLowerCase || !hasUpperCase || !hasDigit || !hasSpecialChar) {
            return Resource.Error("Password does not meet requirements")
        }

        return repository.register(email, fullName, birthDate, identityType, password, passwordConfirm)
    }
}
