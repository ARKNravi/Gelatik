package com.example.bckc.data.model.request

data class ChangePasswordRequest(
    val verification_token: String,
    val new_password: String,
    val new_password_confirm: String
)
