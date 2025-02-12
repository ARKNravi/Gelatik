package com.example.bckc.data.api

import com.example.bckc.data.model.request.LoginRequest
import com.example.bckc.data.model.request.RegisterRequest
import com.example.bckc.data.model.request.VerifyPasswordRequest
import com.example.bckc.data.model.request.ChangePasswordRequest
import com.example.bckc.data.model.response.AuthResponse
import com.example.bckc.data.model.response.UserResponse
import com.example.bckc.data.model.response.VerifyPasswordResponse
import com.example.bckc.data.model.response.ChangePasswordResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("users/profile")
    suspend fun getUserProfile(): Response<UserResponse>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Body request: Map<String, String>
    ): Response<UserResponse>

    suspend fun updateUserProfile(
        fullName: String,
        birthDate: String,
        institution: String,
        profilePictureUrl: String?
    ): Response<UserResponse> {
        val request = mutableMapOf(
            "full_name" to fullName,
            "birth_date" to birthDate,
            "institution" to institution
        )
        profilePictureUrl?.let { request["profile_picture_url"] = it }
        return updateUserProfile(request)
    }

    @POST("users/verify-password")
    suspend fun verifyPassword(
        @Body request: VerifyPasswordRequest
    ): VerifyPasswordResponse

    @POST("users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): ChangePasswordResponse
}
