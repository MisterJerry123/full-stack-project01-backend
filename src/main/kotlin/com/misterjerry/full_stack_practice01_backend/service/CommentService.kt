package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.CommentRequest
import com.misterjerry.full_stack_practice01_backend.dto.CommentResponse
import com.misterjerry.full_stack_practice01_backend.entity.Comment
import com.misterjerry.full_stack_practice01_backend.repository.CommentRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class CommentService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) {

    fun createComment(
        postId: Long,
        authorId: Long,
        commentRequest: CommentRequest
    ): Long {
        val post = postRepository.findById(postId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.") }
        val user = userRepository.findById(authorId)
            .orElseThrow { throw IllegalArgumentException("유효한 유저가 아닙니다.") }

        val comment = Comment(
            content = commentRequest.comment,
            post = post,
            author = user
        )
        return commentRepository.save(comment).id
    }

    fun getComment(postId: Long, commentId: Long): Comment {

        val comment = commentRepository.findById(commentId)
            .orElseThrow {
                IllegalArgumentException("유효한 댓글이 아닙니다.")
            }
        if(comment.post.id!=postId){
            throw ResponseStatusException(HttpStatus.NOT_FOUND,"게시글이 존재하지 않습니다")
        }
        return comment
    }

    fun deleteComment(postId: Long, authorId: Long, commentId: Long): Long {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "유효한 댓글이 아닙니다.") }

        if (postId != comment.post.id) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "게시글 번호가 일치하지 않습니다."
            )
        }

        if (authorId != comment.author.id) { //작성자가 다른경우 삭제 불가능
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "본인 댓글만 삭제 가능합니다."
            )
        }
        commentRepository.deleteById(commentId)
        return comment.id
    }

    fun getComments(postId: Long): List<CommentResponse> {
        if(!postRepository.existsById(postId)){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")
        }


        val comment = commentRepository.findAllByPostId(postId).map { it ->
            CommentResponse(
                id = it.id,
                content = it.content,
                authorId = it.author.id,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
        return comment
    }

    @Transactional
    fun updateComment(commentId: Long, postId: Long, content: String, authorId: Long): Long {
        val comment = commentRepository.findById(commentId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.")
        }
        if(!postRepository.existsById(postId)){
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")
        }
        if (comment.author.id != authorId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "본인 댓글만 수정할 수 있습니다.")
        }
        if (postId != comment.post.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 번호가 일치하지 않습니다.")
        }
        comment.content = content
        comment.updatedAt = LocalDateTime.now()
        commentRepository.save(comment)

        return commentId
    }
}