package com.misterjerry.full_stack_practice01_backend.presentation.dto

import java.time.LocalDateTime

data class CommentResponse (
    val id: Long,
    val content:String,
    val authorId:Long,
    val authorName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)