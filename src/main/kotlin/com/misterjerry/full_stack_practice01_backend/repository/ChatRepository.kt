package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<Chat, Long> {
    fun findByChattingRoomIdOrderByCreatedAtAsc(chattingRoomId: Long):List<Chat>
}