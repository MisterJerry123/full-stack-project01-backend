package com.misterjerry.full_stack_practice01_backend.presentation.controller

import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChattingRoomResponse
import com.misterjerry.full_stack_practice01_backend.presentation.dto.CreateChattingRoomRequest
import com.misterjerry.full_stack_practice01_backend.presentation.dto.SendChatRequest
import com.misterjerry.full_stack_practice01_backend.domain.service.ChatService
import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChatsResponse
import com.misterjerry.full_stack_practice01_backend.presentation.dto.GetChattingRoomsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val jwtDecoder: JwtDecoder
) {
    @Operation(
        summary = "새로운 채팅방 생성",
        description = "새로운 채팅방을 생성합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/create")
    fun createChattingRoom(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreateChattingRoomRequest
    ): ResponseEntity<GetChattingRoomResponse> {
        val myId = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.CREATED).body(
            chatService.createChattingRoom(myId = myId, request.targetId)
        )
    }


    @MessageMapping("/message")
    fun sendChatting(
        @Header("Authorization") authHeader: String,
        @Payload request: SendChatRequest,
    ) {
        println("🔥🔥🔥🔥🔥 [ChatController] sendChatting 함수 실행됨! 요청: ${request.content}")
        val token = authHeader.removePrefix("Bearer ")
        val jwt = jwtDecoder.decode(token)
        val myId = jwt.subject?.toLong() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")

        val response = chatService.sendChatting(
            content = request.content,
            myId = myId,
            targetChattingRoomId = request.chattingRoomId
        )
        messagingTemplate.convertAndSend("/sub/api/chats/${request.chattingRoomId}", response)
    }

    @Operation(
        summary = "채팅방 조회",
        description = "내가 속한 채팅방을 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    fun getChattingRooms(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<GetChattingRoomsResponse> {
        val myId = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getChattingRooms(myId))
    }

    @Operation(
        summary = "채팅 조회",
        description = "채팅방ID에 해당하는 채팅을 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{chattingRoomId}/messages")
    fun getChats(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable chattingRoomId: Long,
    ): ResponseEntity<GetChatsResponse> {
        val myId = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getChats(myId, chattingRoomId))
    }
}
