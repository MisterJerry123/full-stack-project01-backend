package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.CommentRequest
import com.misterjerry.full_stack_practice01_backend.service.CommentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/posts")
class CommentController(
    private val commentService: CommentService
) {

    @Operation(summary = "해당 게시글에 댓글 작성", description = "게시글 번호에 맞는 댓글을 추가합니다.")
    @SecurityRequirement(name = "bearerAuth")

    @PostMapping("/{postId}/comments")
    fun createComments(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @RequestBody @Valid request: CommentRequest
    ): ResponseEntity<Long> {
        val author = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효하지 않은 유저입니다.")


        return ResponseEntity.status(HttpStatus.CREATED).body(
            commentService.createComment(
                commentRequest = request,
                postId = postId,
                authorId = author
            )
        )

    }
}