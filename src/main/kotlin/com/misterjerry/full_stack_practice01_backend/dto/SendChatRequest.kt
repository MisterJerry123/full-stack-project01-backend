package com.misterjerry.full_stack_practice01_backend.dto

data class SendChatRequest(
    val chattingRoomId: Long,
    val content: String = ""
)