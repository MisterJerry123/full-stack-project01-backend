
package com.misterjerry.full_stack_practice01_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    HttpMethod.POST,
                    "/api/users/signUp",
                    "/api/users/login",

                ).permitAll()

                it.requestMatchers(
                    HttpMethod.GET,
                    "/health",
                    "/bmtn"
                ).permitAll()

                it.requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                it.anyRequest().authenticated()
            }
        http.oauth2ResourceServer {
            it.jwt { }
        }
        return http.build()
    }
}
