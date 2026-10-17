package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.GetPostResponse
import com.misterjerry.full_stack_practice01_backend.dto.GetPostsResponse
import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.dto.UpdatePostRequest
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/posts")
class PostController(
    private val postService: PostService
) {
    @Operation(
        summary = "게시글 작성",
        description = "로그인한 사용자가 새 게시글을 작성합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    fun createPost(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: PostRequest,
    ): ResponseEntity<Long> {
        val authorId = jwt.subject?.toLongOrNull() ?: throw IllegalStateException("유효하지 않은 유저입니다.")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(postService.createPost(postRequest = request, authorId = authorId))
    }

    @Operation(
        summary = "게시글 단건 조회",
        description = "게시글 번호로 게시글을 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{postId}")
    fun searchPost(
        @PathVariable postId: Long
    ): ResponseEntity<GetPostResponse> {

        val post = postService.searchPost(postId)
        return ResponseEntity.ok(GetPostResponse(post))
    }


    @Operation(
        summary = "게시글 수정",
        description = "게시글을 수정합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{postId}")
    fun updatePost(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: UpdatePostRequest,
        @PathVariable postId: Long
    ): ResponseEntity<Long> {
        val authorId =
            jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")

        return ResponseEntity.status(HttpStatus.OK)
            .body(postService.updatePost(request, postId, authorId))
    }

    @Operation(
        summary = "게시글 삭제",
        description = "게시글을 삭제합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{postId}")
    fun deletePost(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long
    ): ResponseEntity<Long> {
        val authorId =
            jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId, authorId))

    }

    @Operation(
        summary = "게시글 전체 조회",
        description = "page와 page 크기를 입력받는다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    fun getPosts(
        @RequestParam page: Int,
        @RequestParam size: Int = 10
    ): ResponseEntity<GetPostsResponse> {

        val result = postService.getPosts(page, size)
        return ResponseEntity.status(HttpStatus.OK).body(
            GetPostsResponse(
                result.content,
                totalPages = result.totalPages,
                totalElements = result.totalElements,
                currentPage = result.number
            )
        )
    }

}