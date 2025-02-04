package com.example.bckc.data.model.response

data class UserResponse(
    val id: Int,
    val email: String,
    val full_name: String,
    val birth_date: String,
    val identity_type: String,
    val institution: String?,
    val profile_picture_url: String?,
    val points: Int
)
