package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.UploadImageResponse
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import com.misterjerry.full_stack_practice01_backend.service.ImageService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var imageService: ImageService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockFile: MockMultipartFile
    private lateinit var user: User
    private val imageUrl = "/api/images"

    @BeforeEach
    fun setUp() {
        user = User(
            email = "test@test.com",
            password = "testtest",
            name = "이미지 테스트"
        )
        userRepository.save(user)


        mockFile = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "mock file content".toByteArray()

        )
    }

    //사진 업로드
    @Test
    fun `토큰이 없으면 사진 업로드가 안된다`() {
        mockMvc.perform(
            multipart(imageUrl)
                .file(mockFile)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `파일 형식이 이미지가 아니면 사진 업로드가 안된다`() {
        val wrongFile = MockMultipartFile(
            "file",
            "wrongFile.exe",
            "application/octet-stream",
            "wrong file content".toByteArray()
        )

        mockMvc.perform(
            multipart(imageUrl)
                .file(wrongFile)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `용량이 5mb를 초과한 사진은 업로드가 안된다`() {
        val sixMegaBytes = ByteArray(6 * 1024 * 1024)
        val overFile = MockMultipartFile(
            "file",
            "wrongFile.jpg",
            "image/jpeg",
            sixMegaBytes
        )
        mockMvc.perform(
            multipart(imageUrl)
                .file(overFile)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isContentTooLarge)
    }

    @Test
    fun `빈 파일은 사진 업로드가 안된다`() {
        val emptyFile = MockMultipartFile(
            "file",
            "wrongFile.exe",
            "image/jpeg",
            ByteArray(0)
        )
        mockMvc.perform(
            multipart(imageUrl)
                .file(emptyFile)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `사진 업로드가 된다`() {
        val result = mockMvc.perform(
            multipart(imageUrl)
                .file(mockFile)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.imageUrl").exists())
            .andReturn()
        val response = objectMapper.readValue(result.response.contentAsString, UploadImageResponse::class.java)
        imageService.deleteImage(response.imageUrl)


    }

}