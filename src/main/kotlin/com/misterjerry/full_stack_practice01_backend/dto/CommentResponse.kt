package com.misterjerry.full_stack_practice01_backend.dto

import java.time.LocalDateTime

data class CommentResponse (
    val id: Long,

    val content:String,

    val authorId:Long,

    val createdAt: LocalDateTime,

    val updatedAt: LocalDateTime

)