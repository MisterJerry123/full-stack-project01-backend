package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.GetPostResponse
import com.misterjerry.full_stack_practice01_backend.dto.GetPostsResponse
import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.dto.UpdatePostRequest
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val imageService: ImageService
) {
    fun createPost(postRequest: PostRequest, authorId: Long): Long {
        val user = userRepository.findById(authorId)
            .orElseThrow { throw IllegalStateException("유효하지 않은 유저입니다.") }
        val post = Post(
            title = postRequest.title,
            content = postRequest.content,
            author = user,
            imageUrl = postRequest.imageUrl
        )

        return postRepository.save(post).id
    }

    fun searchPost(postId: Long): GetPostResponse {
        val post = postRepository.findById(postId)
            .map {
                GetPostResponse(
                    title = it.title,
                    content = it.content,
                    authorName = it.author.name,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    imageUrl = it.imageUrl
                )
            }
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.")
            }
        return post
    }

    @Transactional
    fun updatePost(updatePostRequest: UpdatePostRequest, postId: Long, authorId: Long): Long {
        val post = postRepository.findById(postId)
            .orElseThrow {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다.")
            }

        if (post.author.id != authorId) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "본인의 게시글만 수정할 수 있습니다."
            )
        }
        post.title = updatePostRequest.title
        post.content = updatePostRequest.content
        post.imageUrl = updatePostRequest.imageUrl

        return post.id
    }

    @Transactional
    fun deletePost(postId: Long, authorId: Long): Long {
        val post = postRepository.findById(postId)
            .orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND, "해당하는 게시글이 없습니다.") }

        if (post.author.id != authorId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 게시글만 삭제할 수 있습니다.")
        }
        postRepository.delete(post)
        post.imageUrl?.let {
            imageService.deleteImage(it)
        }
        return postId
    }

    fun getPosts(page: Int, size: Int): GetPostsResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val postList = postRepository.findAll(pageable).map {
            GetPostResponse(
                title = it.title,
                content = it.content,
                authorName = it.author.name,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                imageUrl = it.imageUrl
            )
        }
        val posts = GetPostsResponse(
            posts = postList.content,
            totalPages = postList.totalPages,
            totalElements = postList.totalElements,
            currentPage = postList.number
        )

        return posts
    }

}