package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var userRepository: UserRepository
    private lateinit var user: User
    private lateinit var post1: Post
    private lateinit var post2: Post

    private val getPostsUrl = "/api/posts"
    private val getPostUrl = "/api/post"


    @BeforeEach
    fun setUp() {
        user = userRepository.save(
            User(
                email = "test@test.com",
                password = "testtest",
                name = "테스트"
            )
        )
        post1 = postRepository.save(
            Post(
                title = "테스트1",
                content = "테스트1",
                author = user
            )
        )
        post2 = postRepository.save(
            Post(
                title = "테스트2",
                content = "테스트2",
                author = user
            )
        )
    }

    //게시글 전체 조회
    @Test
    fun `토큰이 없으면 게시글 전체가 조회되지 않는다`() {
        mockMvc.perform(
            get(getPostsUrl)
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `토큰이 유효하지 않으면 게시글 전체가 조회되지 않는다`() {
        mockMvc.perform(
            get(getPostsUrl)
                .param("page", "0")
                .header("Authorization", "Bearer wrongjwt")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `정상적인 요청이 오면 페이지와 사이즈에 해당하는 게시글 리스트가 조회된다`() {
        mockMvc.perform(
            get(getPostsUrl)
                .param("page", "0")
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.posts.size()").value(2))
    }

    @Test
    fun `totalPages보다 큰 값이 들어오면 빈 리스트가 리턴된다`() {
        mockMvc.perform(
            get(getPostsUrl)
                .param("page", "300")
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.posts.size()").value(0))
            .andExpect(jsonPath("$.currentPage").value(300))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))
    }


    //게시글 단건 조회
    @Test
    fun `토큰이 없으면 게시글 단건 조회가 되지 않는다`() {
        mockMvc.perform(
            get("$getPostUrl/${post1.id}")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `유효한 토큰과 존재하는 게시물을 조회하면 조회가 된다`(){
        mockMvc.perform(
            get("$getPostUrl/${post1.id}")
                .with(jwt().jwt { it.subject(user.id.toString()) })

        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value(post1.title))
            .andExpect(jsonPath("$.content").value(post1.content))
            .andExpect(jsonPath("$.authorName").value(user.name))

    }

    @Test
    fun `없는 게시글을 조회하면 조회되지 않는다`() {
        mockMvc.perform(
            get("$getPostUrl/404")
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }


}