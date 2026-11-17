package com.misterjerry.full_stack_practice01_backend.presentation.dto

import java.time.LocalDateTime

data class GetChatResponse(
    val id: Long = 0,
    val content: String,
    val createdAt: LocalDateTime,
    val writerId: Long,
    val writerName: String

    )
