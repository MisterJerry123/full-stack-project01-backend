package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.PostLikeResponse
import com.misterjerry.full_stack_practice01_backend.entity.PostLike
import com.misterjerry.full_stack_practice01_backend.repository.PostLikeRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class PostLikeService(
    private val postLikeRepository: PostLikeRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    fun postLike(
        userId: Long,
        postId: Long
    ): PostLikeResponse {

        val isLiked = postLikeRepository.findByPostIdAndUserId(postId = postId, userId = userId)
        if (isLiked == null) {//좋아요 누른 이력이 없는 경우 좋아요

            val user = userRepository.findById(userId).orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "유저가 존재하지 않습니다."
                )
            }
            val post = postRepository.findById(postId).orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "게시글이 존재하지 않습니다."
                )
            }

            if(post.author.id==user.id){
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "자신의 게시글에는 좋아요를 누를 수 없습니다.")
            }

            postLikeRepository.save(
                PostLike(
                    user = user,
                    post = post
                )
            )
            return PostLikeResponse(
                status = true,
                totalLikes = postLikeRepository.countByPostId(postId)
            )
        } else {
            postLikeRepository.delete(isLiked)
            return PostLikeResponse(
                status = false,
                totalLikes = postLikeRepository.countByPostId(postId)
            )
        }


    }


}