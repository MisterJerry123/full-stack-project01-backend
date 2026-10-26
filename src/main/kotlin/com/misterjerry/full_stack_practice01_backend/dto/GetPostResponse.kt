package com.misterjerry.full_stack_practice01_backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class GetPostResponse(
    val id: Long = 0,
    val title: String,
    val content: String,
    val authorName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val imageUrl: String? = null,
    val totalLikes: Long = 0,
    @get:JsonProperty("isLiked")
    val isLiked: Boolean = false,
    val totalComments:Long=0
)