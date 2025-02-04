package com.example.bckc.data.api

import com.example.bckc.data.model.request.LoginRequest
import com.example.bckc.data.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
