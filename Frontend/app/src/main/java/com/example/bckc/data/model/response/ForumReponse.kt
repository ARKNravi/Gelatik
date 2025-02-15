package com.example.bckc.data.model.response

import com.google.gson.annotations.SerializedName

data class ForumListResponse(
    @SerializedName("data")
    val data: List<ForumResponse>
)

data class ForumResponse(
    val id: Int,
    val content: String,
    val title: String,
    val subtitle: String,
    val topic: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("is_published")
    val isPublished: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("like_count")
    val likeCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("bookmark_count")
    val bookmarkCount: Int,
    @SerializedName("has_liked")
    val hasLiked: Boolean,
    @SerializedName("has_bookmarked")
    val hasBookmarked: Boolean,
    val comments: List<Any> // For now using Any since comments structure wasn't provided
)
