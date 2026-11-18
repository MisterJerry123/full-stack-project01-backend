package com.misterjerry.full_stack_practice01_backend.core.interceptor

import com.misterjerry.full_stack_practice01_backend.domain.repository.ChattingRoomRepository
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component

@Component
class ChattingRoomSubscriptionInterceptor(
    private val jwtDecoder: JwtDecoder,
    private val chattingRoomRepository: ChattingRoomRepository
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return null

        if (accessor.command == StompCommand.CONNECT) {
            //헤더에서 Bearer~~ 추출
            val authHeader =
                accessor.getFirstNativeHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }
                    ?: throw BadCredentialsException("Authorization 헤더가 비어있습니다.")
            //Bearer를 제거한 jwt토큰 추출
            val token = authHeader.removePrefix("Bearer ").trim()
            if (token.isBlank()) {
                throw BadCredentialsException("토큰이 비어있습니다.")
            }
            //토큰 검증
            val jwt = try {
                jwtDecoder.decode(token)
            } catch (e: JwtException) {
                throw BadCredentialsException("유효하지 않은 JWT토큰입니다.", e)
            }
            //userId 추출
            val userId =
                jwt.subject?.toLongOrNull() ?: throw BadCredentialsException("유효하지 않은 유저입니다.")

            accessor.user = UsernamePasswordAuthenticationToken(
                userId.toString(),
                null,
                emptyList()
            )
        } else if (accessor.command == StompCommand.SUBSCRIBE) {
            val destination =
                accessor.destination ?: throw BadCredentialsException("경로가 유효하지 않습니다.")

            val userId = accessor.user?.name?.toLongOrNull()
                ?: throw BadCredentialsException("유효하지 않은 유저입니다.")

            val roomId = CHAT_SUB_REGEX.matchEntire(destination)
                ?.groupValues
                ?.getOrNull(1)
                ?.toLongOrNull() ?: throw BadCredentialsException("방 번호가 유효하지 않습니다.")

            if (!chattingRoomRepository.existsParticipant(roomId = roomId, userId)) {
                throw AccessDeniedException("채팅방 참가자가 아닙니다.")
            }
        } else if (accessor.command == StompCommand.SEND) {

            accessor.user?.name?.toLongOrNull() ?: throw BadCredentialsException("유효하지 않은 유저입니다.")

            val destination = accessor.destination
            if (destination != "/pub/message") {
                throw AccessDeniedException("잘못된 요청입니다.")
            }
        }

        return message
    }
    companion object {
        private val CHAT_SUB_REGEX = Regex("^/sub/api/chats/(\\d+)$")
    }
}
