package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.CommentRequest
import com.misterjerry.full_stack_practice01_backend.dto.CommentResponse
import com.misterjerry.full_stack_practice01_backend.entity.Comment
import com.misterjerry.full_stack_practice01_backend.service.CommentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.transaction.Transactional
import jakarta.validation.Valid
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
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

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


    @Operation(summary = "댓글 단건 조회", description = "댓글 번호에 해당하는 댓글 1개를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("{postId}/comments/{commentId}")
    fun getComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @PathVariable commentId: Long
    ): ResponseEntity<Comment> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(commentService.getComment(commentId = commentId))

    }

    @Operation(summary = "댓글 삭제", description = "댓글 번호에 해당하는 댓글을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @Transactional
    @DeleteMapping("{postId}/comments/{commentId}")
    fun deleteComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<Long> {
        val user = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효한 유저가 아닙니다.")

        return ResponseEntity.status(HttpStatus.OK)
            .body(
                commentService.deleteComment(
                    postId = postId,
                    authorId = user,
                    commentId = commentId
                )
            )
    }

    @Operation(summary = "해당 게시글의 댓글 전체 조회", description = "게시글에 해당하는 댓글 전체를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("{postId}/comments")
    fun getComments(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long
    ): ResponseEntity<List<CommentResponse>> {

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getComments(postId))
    }

    @Operation(summary = "댓글 수정", description = "댓글 번호에 해당하는 댓글을 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("{postId}/comments/{commentId}")
    fun updateComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestBody contentRequest: CommentRequest
    ): ResponseEntity<Long> {
        val author = jwt.subject?.toLongOrNull() ?: throw IllegalArgumentException("유효한 사용자가 아닙니다.")
        return ResponseEntity.status(HttpStatus.OK).body(
            commentService.updateComment(
                commentId = commentId,
                postId=postId,
                content = contentRequest.comment,
                authorId = author
            )
        )

    }

}