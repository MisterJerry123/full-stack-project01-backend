package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.CommentRequest
import com.misterjerry.full_stack_practice01_backend.entity.Comment
import com.misterjerry.full_stack_practice01_backend.repository.CommentRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import org.springframework.stereotype.Service

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
        val post = postRepository.findById(postId).orElseThrow { throw IllegalArgumentException("유효한 게시글이 아닙니다.") }
        val user = userRepository.findById(authorId).orElseThrow{throw IllegalArgumentException("유효한 유저가 아닙니다.") }

        val comment = Comment(
            content = commentRequest.comment,
            post = post,
            author = user
        )

        return commentRepository.save(comment).id
    }


}