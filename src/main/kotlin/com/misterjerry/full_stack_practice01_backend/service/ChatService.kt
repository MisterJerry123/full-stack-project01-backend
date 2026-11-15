package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.ChattingRoomResponse
import com.misterjerry.full_stack_practice01_backend.entity.ChattingRoom
import com.misterjerry.full_stack_practice01_backend.repository.ChatRepository
import com.misterjerry.full_stack_practice01_backend.repository.ChattingRoomRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val chattingRoomRepository: ChattingRoomRepository
) {
    fun createChattingRoom(myId: Long, targetId: Long): ChattingRoomResponse {
        if (myId == targetId) {
            throw IllegalStateException("myId와 targetId는 같을 수 없습니다.")
        }
        val targetChattingRoom = chattingRoomRepository.findRoomBetweenUsers(myId, targetId)
        if (targetChattingRoom != null) {
            val partnerName =
                if (targetChattingRoom.user1.id == myId) targetChattingRoom.user2.name else targetChattingRoom.user1.name
            return ChattingRoomResponse(
                id = targetChattingRoom.id,
                partnerName = partnerName,
                createdAt = targetChattingRoom.createdAt,
                lastMessage = targetChattingRoom.lastMessage
            )
        } else {
            val newChattingRoom = chattingRoomRepository.save(
                ChattingRoom(
                    user1 = userRepository.findById(myId).orElseThrow {
                        throw IllegalArgumentException("유저1가 없습니다.")
                    },
                    user2 = userRepository.findById(targetId).orElseThrow {
                        throw IllegalArgumentException("유저2가 없습니다.")
                    }
                )
            )
            val partnerName =
                if (newChattingRoom.user1.id == myId) newChattingRoom.user2.name else newChattingRoom.user1.name
            return ChattingRoomResponse(
                id = newChattingRoom.id,
                partnerName = partnerName,
                createdAt = newChattingRoom.createdAt,
                lastMessage = newChattingRoom.lastMessage
            )
        }
    }
}