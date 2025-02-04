package com.example.bckc.data.model.request

data class RegisterRequest(
    val email: String,
    val full_name: String,
    val birth_date: String,
    val identity_type: String,
    val password: String,
    val password_confirm: String
)
