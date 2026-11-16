package com.misterjerry.full_stack_practice01_backend.presentation.controller

import com.misterjerry.full_stack_practice01_backend.presentation.dto.ChattingRoomResponse
import com.misterjerry.full_stack_practice01_backend.presentation.dto.CreateChattingRoomRequest
import com.misterjerry.full_stack_practice01_backend.presentation.dto.SendChatRequest
import com.misterjerry.full_stack_practice01_backend.domain.service.ChatService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
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
    ): ResponseEntity<ChattingRoomResponse> {
        val myId = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.CREATED).body(
            chatService.createChattingRoom(myId = myId, request.targetId)
        )
    }

    @Operation(
        summary = "해당 채팅방에 채팅 전송",
        description = "해당하는 채팅방에 채팅을 보냅니다."
    )
    @SecurityRequirement(name = "bearerAuth")
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
        messagingTemplate.convertAndSend("/sub/api/chats/${request.chattingRoomId}",response)


    }
}