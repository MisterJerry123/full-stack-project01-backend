package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.GetPostResponse
import com.misterjerry.full_stack_practice01_backend.dto.GetPostsResponse
import com.misterjerry.full_stack_practice01_backend.dto.PostRequest
import com.misterjerry.full_stack_practice01_backend.dto.UpdatePostRequest
import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.repository.CommentRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostLikeRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
    private val postLikeRepository: PostLikeRepository,
    private val commentRepository: CommentRepository
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

    fun getPost(postId: Long, userId: Long): GetPostResponse {
        val post = postRepository.findById(postId).orElseThrow {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."
            )
        }
        val isLiked = postLikeRepository.findByPostIdAndUserId(postId, userId) != null
        val totalLikes = postLikeRepository.countByPostId(postId)
        val response =
            GetPostResponse(
                id=post.id,
                title = post.title,
                content = post.content,
                authorName = post.author.name,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
                imageUrl = post.imageUrl,
                isLiked = isLiked,
                totalLikes = totalLikes
            )

        return response
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

    fun getPosts(page: Int, size: Int, userId: Long): GetPostsResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val postList = postRepository.findAll(pageable).map {
            val isLiked =
                postLikeRepository.findByPostIdAndUserId(postId = it.id, userId = userId) != null
            val totalLiked = postLikeRepository.countByPostId(it.id)
            val totalComments = commentRepository.countByPostId(it.id)

            GetPostResponse(
                id = it.id,
                title = it.title,
                content = it.content,
                authorName = it.author.name,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                imageUrl = it.imageUrl,
                totalLikes = totalLiked,
                isLiked = isLiked,
                totalComments=totalComments
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