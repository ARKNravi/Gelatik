package com.example.bckc.data.model.response

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class TranslationOrderListResponse(
    @SerializedName("items") val items: List<TranslationOrderResponse>,
    @SerializedName("total") val total: Int
)

data class TranslationOrderResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("time_slot") val timeSlot: String,
    @SerializedName("description") val description: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("translator") val translator: TranslatorResponse,
    @SerializedName("user") val user: UserResponse,
    @SerializedName("review") val review: ReviewResponse?
)


data class ReviewResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String
)
