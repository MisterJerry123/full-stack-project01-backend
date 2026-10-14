
package com.misterjerry.full_stack_practice01_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.util.Base64
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    @Value("\${jwt.secret}")
    private val jwtSecret: String
) {

    private fun secretKey(): SecretKeySpec {
        val bytes = Base64.getDecoder().decode(jwtSecret)

        require(bytes.size >= 32) {
            "JWT 비밀키는 최소 256비트여야 합니다."
        }

        return SecretKeySpec(bytes, "HmacSHA256")
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        return NimbusJwtEncoder
            .withSecretKey(secretKey())
            .algorithm(MacAlgorithm.HS256)
            .build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder
            .withSecretKey(secretKey())
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
    }
}
