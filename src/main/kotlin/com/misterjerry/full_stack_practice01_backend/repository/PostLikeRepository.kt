package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.entity.PostLike
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository : JpaRepository<PostLike, Long> {

    fun findByPostIdAndUserId(postId: Long, userId: Long): PostLike?

    fun countByPostId(postId: Long): Long
}