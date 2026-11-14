package com.misterjerry.full_stack_practice01_backend.dto

import java.time.LocalDateTime

data class ChattingRoomResponse(
    val id:Long,
    val partnerName: String,
    val createdAt: LocalDateTime,
    val lastMessage: String? = null
)
