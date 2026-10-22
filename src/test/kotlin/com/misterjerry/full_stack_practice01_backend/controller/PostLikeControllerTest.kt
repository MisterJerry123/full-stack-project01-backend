package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.entity.PostLike
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.PostLikeRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostLikeControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var postLikeRepository: PostLikeRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var userRepository: UserRepository


    private lateinit var user: User
    private lateinit var post: Post
    private val postLikeUrl = "/api/posts/{postId}/likes"

    @BeforeEach
    fun setUp() {
        user = User(
            email = "test@test.com",
            password = "testtest",
            name = "테스트 계정"
        )
        post = Post(
            title = "테스트 게시글",
            content = "테스트 게시글",
            author = user,
        )
        userRepository.save(user)
        postRepository.save(post)
    }

    @Test
    fun `토큰이 없으면 좋아요가 되지 않는다`() {
        mockMvc.perform(
            post(postLikeUrl, post.id)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `존재하지 않는 게시글에는 좋아요를 누를 수 없다`() {
        val anotherUser = User(
            email = "testtest@test.com",
            password = "testtest",
            name = "게시글 작성하지 않은 테스트 계정"
        )
        userRepository.save(anotherUser)
        mockMvc.perform(
            post(postLikeUrl, 404)
                .with(jwt().jwt { it.subject(anotherUser.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `내 게시글에는 좋아요를 누를 수 없다`() {
        mockMvc.perform(
            post(postLikeUrl, post.id)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `내가 작성하지 않은 게시글에는 좋아요를 누를 수 있다`() {
        val anotherUser = User(
            email = "testtest@test.com",
            password = "testtest",
            name = "게시글 작성하지 않은 테스트 계정"
        )
        userRepository.save(anotherUser)
        mockMvc.perform(
            post(postLikeUrl, post.id)
                .with(jwt().jwt { it.subject(anotherUser.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.totalLikes").value(1))
    }

    @Test
    fun `이미 좋아요를 누른 게시글에서 좋아요를 누를 경우 좋아요가 취소된다`() {
        val anotherUser = User(
            email = "testtest@test.com",
            password = "testtest",
            name = "게시글 작성하지 않은 테스트 계정"
        )
        userRepository.save(anotherUser)
        val postLike = PostLike(
            user = anotherUser,
            post = post
        )
        postLikeRepository.save(postLike)
        mockMvc.perform(
            post(postLikeUrl, post.id)
                .with(jwt().jwt { it.subject(anotherUser.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.totalLikes").value(0))
    }
}