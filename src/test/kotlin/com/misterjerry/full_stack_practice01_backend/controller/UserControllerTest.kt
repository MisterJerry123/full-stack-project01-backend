package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.SignUpRequest
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val signUpUrl = "/api/users/signUp"

    @Test
    fun `이메일, 비밀번호, 이름을 입력하면 회원가입이 되어야한다`() {
        val request = SignUpRequest(
            email = "test@test.com",
            password = "testtest",
            name = "테스트"
        )

        val json = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            post(signUpUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)

        ).andExpect(status().isCreated)
    }

    @Test
    fun `이미 가입된 아이디로는 회원가입이 안된다`() {
        val request = SignUpRequest(
            email = "test@test.com",
            password = "testtest",
            name = "테스트"
        )
        val json = objectMapper.writeValueAsString(request)
        mockMvc.perform(
            post(signUpUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)

        mockMvc.perform(
            post(signUpUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `이메일 형식에 맞지 않으면 회원가입이 되지 않는다`() {
        val request = SignUpRequest(
            email = "test",
            password = "testtest",
            name = "테스트"
        )
        val json = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            post(signUpUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `이름에 공백만 있으면 회원가입이 되지 않는다`() {
        val request = SignUpRequest(
            email = "test@test.com",
            password = "testtest",
            name = " "
        )

        val json = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            post(signUpUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `비밀번호에 공백만 있으면 회원가입이 되지 않는다`() {
        val request = SignUpRequest(
            email = "test@test.com",
            password = " ",
            name = "test"
        )

        val json = objectMapper.writeValueAsString(request)

        mockMvc.perform(
            post(signUpUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isBadRequest)
    }


}