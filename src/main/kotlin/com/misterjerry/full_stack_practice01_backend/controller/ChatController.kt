package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.ChattingRoomResponse
import com.misterjerry.full_stack_practice01_backend.dto.CreateChattingRoomRequest
import com.misterjerry.full_stack_practice01_backend.service.ChatService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService
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
    ): ResponseEntity<ChattingRoomResponse>{
        val myId = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.CREATED).body(
            chatService.createChattingRoom(myId = myId, request.targetId)
        )
    }
}