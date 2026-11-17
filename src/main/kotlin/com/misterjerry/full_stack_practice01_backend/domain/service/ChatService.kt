package com.misterjerry.full_stack_practice01_backend.domain.service

import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChatResponse
import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChattingRoomResponse
import com.misterjerry.full_stack_practice01_backend.data.entity.Chat
import com.misterjerry.full_stack_practice01_backend.data.entity.ChattingRoom
import com.misterjerry.full_stack_practice01_backend.domain.repository.ChatRepository
import com.misterjerry.full_stack_practice01_backend.domain.repository.ChattingRoomRepository
import com.misterjerry.full_stack_practice01_backend.domain.repository.UserRepository
import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChatsResponse
import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChattingRoomsResponse
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ChatService(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val chattingRoomRepository: ChattingRoomRepository
) {
    fun createChattingRoom(myId: Long, targetId: Long): GetChattingRoomResponse {
        if (myId == targetId) {
            throw IllegalStateException("myId와 targetId는 같을 수 없습니다.")
        }
        val targetChattingRoom = chattingRoomRepository.findRoomBetweenUsers(myId, targetId)
        if (targetChattingRoom != null) {
            val partnerName =
                if (targetChattingRoom.user1.id == myId) targetChattingRoom.user2.name else targetChattingRoom.user1.name
            return GetChattingRoomResponse(
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
            return GetChattingRoomResponse(
                id = newChattingRoom.id,
                partnerName = partnerName,
                createdAt = newChattingRoom.createdAt,
                lastMessage = newChattingRoom.lastMessage
            )
        }
    }

    @Transactional
    fun sendChatting(content: String, myId: Long, targetChattingRoomId: Long): GetChatResponse {
        val targetChattingRoom = chattingRoomRepository.findById(targetChattingRoomId)
            .orElseThrow { throw IllegalArgumentException("유효하지 않은 채팅방입니다.") }
        if (!(targetChattingRoom.user1.id == myId || targetChattingRoom.user2.id == myId)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "본인이 속하지 않은 채팅방은 접근할 수 없습니다."
            )
        }
        val sender = userRepository.findById(myId)
            .orElseThrow { throw IllegalArgumentException("유효하지 않은 유저입니다.") }

        val chatting = Chat(
            sender = sender,
            chattingRoom = targetChattingRoom,
            content = content,
        )
        val savedChat = chatRepository.save(chatting)
        targetChattingRoom.lastMessage = content
        targetChattingRoom.lastMessageCreatedAt = savedChat.createdAt

        return GetChatResponse(
            id = savedChat.id,
            content = savedChat.content,
            createdAt = savedChat.createdAt,
            writerId = sender.id,
            writerName = sender.name,
        )
    }

    fun getChattingRooms(myId: Long): GetChattingRoomsResponse {
        val chattingRooms = chattingRoomRepository.findByUserId(myId).map {
            val partner = if (it.user1.id == myId) it.user2 else it.user1
            GetChattingRoomResponse(
                id = it.id,
                partnerName = partner.name,
                createdAt = it.createdAt,
                lastMessage = it.lastMessage,
                lastMessageCreatedAt = it.lastMessageCreatedAt
            )
        }
        return GetChattingRoomsResponse(
            chattingRooms = chattingRooms
        )

    }

    fun getChats(myId: Long, targetChattingRoomId: Long): GetChatsResponse {
        val chattingRoom = chattingRoomRepository.findById(targetChattingRoomId).orElseThrow {
            ResponseStatusException(
                HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."
            )
        }
        if(!(chattingRoom.user1.id==myId || chattingRoom.user2.id==myId)){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방 소유자가 아닙니다.")
        }


        val chats =
            chatRepository.findByChattingRoomIdOrderByCreatedAtAsc(targetChattingRoomId).map {
                GetChatResponse(
                    id = it.id,
                    content = it.content,
                    createdAt = it.createdAt,
                    writerId = it.sender.id,
                    writerName = it.sender.name
                )
            }
        return GetChatsResponse(
            chats = chats
        )

    }
}