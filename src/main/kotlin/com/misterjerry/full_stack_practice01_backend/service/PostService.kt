package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val postRepository: PostRepository
) {
    fun createPost(postRequest: PostRequest, authorId: Long): Long {

        val post = Post(
            title = postRequest.title,
            content = postRequest.content,
            authorId = authorId,
        )

        return postRepository.save(post).id
    }

    fun searchPost(postId: Long): Post {
        return postRepository.findById(postId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.")

            }
    }
}