package com.misterjerry.full_stack_practice01_backend.core.config

import com.misterjerry.full_stack_practice01_backend.core.interceptor.ChattingRoomSubscriptionInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val chattingRoomSubscriptionInterceptor: ChattingRoomSubscriptionInterceptor
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns(
                "*"
//                "http://localhost:5173",
//                "https://full-stack-project01-frontend-hazel.vercel.app"
            )
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub")
        registry.setApplicationDestinationPrefixes("/pub")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(chattingRoomSubscriptionInterceptor)


    }
}