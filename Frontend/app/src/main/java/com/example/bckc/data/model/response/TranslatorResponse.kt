package com.example.bckc.data.model.response

import com.google.gson.annotations.SerializedName

data class TranslatorListResponse(
    @SerializedName("items")
    val items: List<TranslatorResponse>,
    @SerializedName("total")
    val total: Int
)

data class TranslatorResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("alamat")
    val alamat: String,
    @SerializedName("availability")
    val availability: Boolean,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int
)
