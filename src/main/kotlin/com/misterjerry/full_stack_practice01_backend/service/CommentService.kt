package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.CommentRequest
import com.misterjerry.full_stack_practice01_backend.entity.Comment
import com.misterjerry.full_stack_practice01_backend.repository.CommentRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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
            .orElseThrow { throw IllegalArgumentException("유효한 게시글이 아닙니다.") }
        val user = userRepository.findById(authorId)
            .orElseThrow { throw IllegalArgumentException("유효한 유저가 아닙니다.") }

        val comment = Comment(
            content = commentRequest.comment,
            post = post,
            author = user
        )
        return commentRepository.save(comment).id
    }

    fun getComment(commentId: Long): Comment{
        return commentRepository.findById(commentId).orElseThrow { IllegalArgumentException("유효한 댓글이 아닙니다.") }
    }
    fun deleteComment(postId:Long, authorId:Long, commentId: Long): Long {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND,"유효한 댓글이 아닙니다.") }

        if(postId!=comment.post.id){
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
}