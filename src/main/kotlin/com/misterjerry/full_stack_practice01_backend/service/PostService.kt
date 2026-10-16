package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.dto.UpdatePostRequest
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    @Transactional
    fun updatePost(updatePostRequest: UpdatePostRequest, postId: Long, authorId: Long): Long {
        val post = postRepository.findById(postId)
            .orElseThrow {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.")
            }

        if (post.authorId != authorId) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "본인의 게시글만 수정할 수 있습니다."
            )
        }
        post.title = updatePostRequest.title
        post.content = updatePostRequest.content

        return post.id
    }
    @Transactional
    fun deletePost(postId: Long, authorId: Long): Long {
        val post = postRepository.findById(postId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 게시글이 없습니다.") }

        if (post.authorId != authorId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 게시글만 삭제할 수 있습니다.")
        }
        postRepository.delete(post)
        return postId
    }

    fun getPosts(page:Int,size:Int): Page<Post> {
        val pageable = PageRequest.of(page, size)
        return postRepository.findAll(pageable)
    }

}