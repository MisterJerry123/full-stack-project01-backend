package com.misterjerry.full_stack_practice01_backend.presentation.dto

import java.time.LocalDateTime

data class GetChattingRoomResponse(
    val id: Long,
    val partnerName: String,
    val createdAt: LocalDateTime,
    val lastMessage: String? = null,
    val lastMessageCreatedAt: LocalDateTime? = null
)
