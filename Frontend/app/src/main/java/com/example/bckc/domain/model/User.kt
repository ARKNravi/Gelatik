package com.example.bckc.domain.model

data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val birthDate: String,
    val identityType: String,
    val institution: String?,
    val profilePictureUrl: String?,
    val points: Int
)
