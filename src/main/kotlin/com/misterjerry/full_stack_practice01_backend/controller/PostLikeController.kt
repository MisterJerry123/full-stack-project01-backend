package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.PostLikeResponse
import com.misterjerry.full_stack_practice01_backend.service.PostLikeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/posts")
class PostLikeController(
    private val postLikeService: PostLikeService
) {

    @Operation(summary = "게시글 좋아요 혹은 취소", description = "게시글 좋아요 이력이 없으면 좋아요. 있으면 좋아요를 취소합니다")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{postId}/likes")
    fun togglePostLike(
        @PathVariable postId: Long,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<PostLikeResponse> {

        val userId = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")
        return ResponseEntity.status(HttpStatus.OK)
            .body(postLikeService.postLike(userId = userId, postId = postId))
    }
}