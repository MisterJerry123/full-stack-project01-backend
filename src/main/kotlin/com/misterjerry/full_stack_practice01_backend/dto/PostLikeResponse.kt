package com.misterjerry.full_stack_practice01_backend.dto

data class PostLikeResponse(
    val status: Boolean,
    val totalLikes: Long
)