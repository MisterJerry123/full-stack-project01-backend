package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.CommentRequest
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
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
    private val getCommentsUrl = "/api/posts/{postId}/comments"
    private val createCommentUrl = "/api/posts/{postId}/comments"
    private val updateCommentUrl = "/api/posts/{postId}/comments/{commentId}"

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
                author = user,
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
            get(getCommentUrl, post.id, comment.id)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").value(comment.content))
    }

    //댓글 전체 조회
    @Test
    fun `토큰이 없는 경우 댓글들이 조회되지 않는다`() {
        mockMvc.perform(
            get(getCommentsUrl, post.id)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `게시글이 존재하지 않는 댓글은 조회되지 않는다`() {
        mockMvc.perform(
            get(getCommentsUrl, 404)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `토큰이 있고 게시글이 있고 댓글이 있는 경우 조회가 된다`() {
        mockMvc.perform(
            get(getCommentsUrl, post.id)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].content").value(comment.content))
    }

    @Test
    fun `게시글은 존재하나 댓글이 없는 경우는 빈 리스트가 리턴된다`() {
        mockMvc.perform(
            get(getCommentsUrl, post2.id)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(0))
    }

    //댓글 작성 테스트
    @Test
    fun `토큰이 없는 경우 댓글이 작성되지 않는다`() {
        val comment = CommentRequest(
            comment = "댓글 작성 테스트"
        )
        val json = objectMapper.writeValueAsString(comment)

        mockMvc.perform(
            post(createCommentUrl, post.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `게시글이 없는 글에는 댓글이 작성되지 않는다`() {
        val comment = CommentRequest(
            comment = "댓글 작성 테스트"
        )
        val json = objectMapper.writeValueAsString(comment)

        mockMvc.perform(
            post(createCommentUrl, 404)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `토큰이 있고 존재하는 게시글에 댓글이 작성된다`() {
        val comment = CommentRequest(
            comment = "댓글 작성 테스트"
        )
        val json = objectMapper.writeValueAsString(comment)

        mockMvc.perform(
            post(createCommentUrl, post.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `빈 댓글은 작성할 수 없다`() {
        val comment = CommentRequest(
            comment = ""
        )
        val json = objectMapper.writeValueAsString(comment)

        mockMvc.perform(
            post(createCommentUrl, post.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `화이트 스페이스만 있는 댓글은 작성할 수 없다`() {
        val comment = CommentRequest(
            comment = "   "
        )
        val json = objectMapper.writeValueAsString(comment)

        mockMvc.perform(
            post(createCommentUrl, post.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    //댓글 수정
    @Test
    fun `토큰이 없으면 댓글 수정이 안된다`(){
        val updatedComment = CommentRequest(
            comment = "수정된 댓글"
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,post.id,comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        )
            .andExpect(status().isUnauthorized)
    }
    @Test
    fun `게시글이 없으면 댓글 수정이 안된다`(){
        val updatedComment = CommentRequest(
            comment = "수정된 댓글"
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,404,comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }
    @Test
    fun `원 댓글 게시글 번호가 다르면 댓글 수정이 안된다`(){
        val updatedComment = CommentRequest(
            comment = "수정된 댓글"
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,post2.id,comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isForbidden)
    }
    @Test
    fun `댓글 게시자가 다르면 댓글 수정이 안된다`(){
        val updatedComment = CommentRequest(
            comment = "수정된 댓글"
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,post.id, comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .with(jwt().jwt { it.subject(user2.id.toString()) })
        )
            .andExpect(status().isForbidden)
    }
    @Test
    fun `내가 작성한 댓글은 수정 가능하다`(){
        val updatedComment = CommentRequest(
            comment = "수정된 댓글"
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,post.id, comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)

        mockMvc.perform(get(getCommentUrl,post.id, comment.id)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").value(updatedComment.comment))
    }
    @Test
    fun `빈 댓글로 수정할 수 없다`(){
        val updatedComment = CommentRequest(
            comment = ""
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,post.id, comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)

    }
    @Test
    fun `화이트 스페이스만 있는 댓글로 수정할 수 없다`(){
        val updatedComment = CommentRequest(
            comment = "  "
        )
        val json = objectMapper.writeValueAsString(updatedComment)

        mockMvc.perform(patch(updateCommentUrl,post.id, comment.id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }



}