package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.entity.Comment
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.CommentRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var commentRepository: CommentRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var user: User
    private lateinit var user2: User
    private lateinit var post: Post
    private lateinit var post2: Post
    private lateinit var comment: Comment

    private val getCommentUrl = "/api/posts/{postId}/comments/{commentId}"

    @BeforeEach
    fun setUp() {

        user = userRepository.save(
            User(
                email = "test@test.com",
                password = "testtest",
                name = "테스트 계정"
            )
        )
        user2 = userRepository.save(
            User(
                email = "testtest@test.com",
                password = "testtest",
                name = "댓글 테스트 계정"
            )
        )
        post = postRepository.save(
            Post(
                title = "테스트 게시글",
                content = "테스트 내용",
                author = user,
            )
        )
        post2 = postRepository.save(
            Post(
                title = "테스트 게시글",
                content = "테스트 내용",
                author = user,
            )
        )
        comment = commentRepository.save(
            Comment(
                author = user2,
                post = post,
                content = "댓글 테스트",
            )
        )


    }

    //댓글 단건 조회
    @Test
    fun `토큰이 없는 경우 댓글이 조회되지 않는다`() {
        mockMvc.perform(get(getCommentUrl, post.id, comment.id))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `게시글이 존재하지 않는 글의 댓글은 조회되지 않는다`() {
        mockMvc.perform(
            get(getCommentUrl, 404, comment.id)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }
    @Test
    fun `토큰이 있고 게시글이 존재하는 글의 댓글은 조회된다`() {
        mockMvc.perform(
            get(getCommentUrl,post.id, comment.id)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
    }

}