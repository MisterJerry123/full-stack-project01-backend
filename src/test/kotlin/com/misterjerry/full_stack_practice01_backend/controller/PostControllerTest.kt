package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.dto.UpdatePostRequest
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

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

    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private lateinit var user: User
    private lateinit var post1: Post
    private lateinit var post2: Post
    private lateinit var post3: Post

    private val getPostsUrl = "/api/posts"
    private val getPostUrl = "/api/posts"
    private val createPostUrl = "/api/posts"
    private val patchPostUrl = "/api/posts"
    private val deletePostUrl = "/api/posts"


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
    fun `유효한 토큰과 존재하는 게시물을 조회하면 조회가 된다`() {
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

    //게시글 작성
    @Test
    fun `토큰이 없으면 게시글 작성이 되지 않는다`() {
        val newPost = PostRequest(
            title = "게시글 작성 테스트",
            content = "게시글 작성 테스트",
        )
        val json = objectMapper.writeValueAsString(newPost)
        mockMvc.perform(
            post(createPostUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `유효한 토큰인 경우 게시글 작성이 된다`() {
        val newPost = PostRequest(
            title = "게시글 작성 테스트",
            content = "게시글 작성 테스트",
        )
        val json = objectMapper.writeValueAsString(newPost)
        mockMvc.perform(
            post(createPostUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `유효한 토큰인 경우 이미지가 포함된 게시글 작성이 된다`() {
        val newPost = PostRequest(
            title = "게시글 작성 테스트",
            content = "게시글 작성 테스트",
            imageUrl = "/uploads/test.jpg"
        )
        val json = objectMapper.writeValueAsString(newPost)
        val result = mockMvc.perform(
            post(createPostUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isCreated)
            .andReturn()

        val response = result.response.contentAsString.toLong()

        val post = postRepository.findById(response).get()
        assertEquals(newPost.imageUrl, post.imageUrl)
    }

    @Test
    fun `제목에 공백만 있는 경우 게시글 작성이 되지 않는다`() {
        val newPost = PostRequest(
            title = " ",
            content = "게시글 작성 테스트",
        )
        val json = objectMapper.writeValueAsString(newPost)
        mockMvc.perform(
            post(createPostUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `게시글 내용에 공백만 있는 경우 게시글 작성이 된다`() {
        val newPost = PostRequest(
            title = "게시글 작성 테스트",
            content = " ",
        )
        val json = objectMapper.writeValueAsString(newPost)
        mockMvc.perform(
            post(createPostUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `게시글 내용이 없는 경우 게시글 작성이 된다`() {
        val newPost = PostRequest(
            title = "게시글 작성 테스트",
            content = "",
        )
        val json = objectMapper.writeValueAsString(newPost)
        mockMvc.perform(
            post(createPostUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isCreated)
    }

    //게시글 수정
    @Test
    fun `내가 작성한 게시글이 아닌 경우 수정이 되지 않는다`() {
        val updatedPost = UpdatePostRequest(
            title = "게시글 수정",
            content = "게시글 수정"
        )
        val anotherUser = userRepository.save(
            User(
                email = "wrongUser",
                password = "testtest",
                name = "다른 유저"
            )
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/${post1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(anotherUser.id.toString()) })
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `토큰이 없으면 내가 작성한 게시글도 수정이 되지 않는다`() {
        val updatedPost = UpdatePostRequest(
            title = "게시글 수정",
            content = "게시글 수정"
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/${post1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `유효한 토큰이 있을 때는 내가 작성한 게시글이 수정된다`() {
        val updatedPost = UpdatePostRequest(
            title = "게시글 수정",
            content = "게시글 수정"
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/${post1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
            .andExpect(content().string(post1.id.toString()))
    }

    @Test
    fun `없는 게시물을 수정하려고 하는 경우 404가 뜬다`() {
        val updatedPost = UpdatePostRequest(
            title = "게시글 수정",
            content = "게시글 수정"
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/404")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `제목에 공백만 있는 경우 수정이 되지 않는다`() {
        val updatedPost = UpdatePostRequest(
            title = " ",
            content = "게시글 수정"
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/${post1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `내용에 공백만 있는 경우 수정이 된다`() {
        val updatedPost = UpdatePostRequest(
            title = "게시글 수정",
            content = "  "
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/${post1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `내용이 없는 경우 수정이 된다`() {
        val updatedPost = UpdatePostRequest(
            title = "게시글 수정",
            content = ""
        )
        val json = objectMapper.writeValueAsString(updatedPost)
        mockMvc.perform(
            patch("$patchPostUrl/${post1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
    }


    //게시글 삭제
    @Test
    fun `내 게시글이 아닌 게시글은 삭제되지 않는다`() {
        val anotherUser = userRepository.save(
            User(
                email = "anotherUser@test.com",
                password = "testtest",
                name = "다른유저"
            )
        )
        mockMvc.perform(
            delete("$deletePostUrl/${post1.id}")
                .with(jwt().jwt { it.subject(anotherUser.id.toString()) })
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `내 게시글이어도 토큰이 없으면 삭제되지 않는다`() {
        mockMvc.perform(delete("$deletePostUrl/${post1.id}"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `내 게시글이고 토큰이 있는 경우 삭제가 된다`() {
        mockMvc.perform(
            delete("$deletePostUrl/${post1.id}")
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `내 게시글에 사진이 있는 경우 토큰이 있는 경우 게시글과 사진 모두 삭제 된다`() {
        //파일 및 게시글 생성
        val fileName = "test_to_delete.png"
        val targetPath = Paths.get("uploads").toAbsolutePath().normalize().resolve(fileName)
        Files.createDirectories(targetPath.parent)
        Files.write(targetPath, "fake image content".toByteArray())
        assertTrue(Files.exists(targetPath))

        val imagePost = postRepository.save(
            Post(
                title = "테스트2",
                content = "테스트2",
                author = user,
                imageUrl = "/uploads/$fileName"
            )
        )
        //게시글 제거
        mockMvc.perform(
            delete("$deletePostUrl/${imagePost.id}")
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isOk)

        assertTrue(postRepository.findById(imagePost.id).isEmpty)
        assertFalse(Files.exists(targetPath))
    }

    @Test
    fun `없는 게시글을 삭제하려고 하면 404가 뜬다`() {
        mockMvc.perform(
            delete("$deletePostUrl/404")
                .with(jwt().jwt { it.subject(user.id.toString()) })
        )
            .andExpect(status().isNotFound)
    }


    //getPosts에서 각 게시글에 totalLikes와 isLike 검증하는 코드 추가
    //getPost에서 게시글에 totalLikes와 isLike 검증하는 코드 추가

}