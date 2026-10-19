package com.misterjerry.full_stack_practice01_backend.controller

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET health 요청시 200 OK와 'OK'가 리턴되어야한다`() {
        mockMvc.perform(get("/health"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().string("OK"))
    }

    @Test
    fun `Get bmtn 요청시 200 OK와 '바멍똥남 메롱'이 리턴되어야한다`() {
        mockMvc.perform(get("/bmtn"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().string("바멍똥남 메롱"))
    }

}