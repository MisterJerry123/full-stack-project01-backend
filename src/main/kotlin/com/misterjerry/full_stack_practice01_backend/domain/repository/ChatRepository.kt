package com.misterjerry.full_stack_practice01_backend.domain.repository

import com.misterjerry.full_stack_practice01_backend.data.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<Chat, Long> {
    fun findByChattingRoomIdOrderByCreatedAtAsc(chattingRoomId: Long):List<Chat>
}