package com.misterjerry.full_stack_practice01_backend.presentation.dto

data class SendChatRequest(
    val chattingRoomId: Long,
    val content: String = ""
)