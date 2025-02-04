package com.example.bckc.data.api

import com.example.bckc.data.model.request.LoginRequest
import com.example.bckc.data.model.request.RegisterRequest
import com.example.bckc.data.model.response.AuthResponse
import com.example.bckc.data.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserResponse>
}
