package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/posts")
class PostController(
    private val postService: PostService
) {
    @PostMapping("/create")
    fun createPost(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: PostRequest,
    ): ResponseEntity<Long> {
        val authorId = jwt.subject?.toLongOrNull() ?: throw IllegalStateException("유효하지 않은 유저입니다.")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(postService.createPost(postRequest = request, authorId = authorId))
    }

}