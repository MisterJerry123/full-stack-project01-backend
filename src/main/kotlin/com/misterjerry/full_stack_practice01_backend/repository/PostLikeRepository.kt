package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.dto.PostCountDto
import com.misterjerry.full_stack_practice01_backend.entity.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostLikeRepository : JpaRepository<PostLike, Long> {

    fun findByPostIdAndUserId(postId: Long, userId: Long): PostLike?

    fun countByPostId(postId: Long): Long
    fun deleteAllByPostId(postId: Long)

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.post.id IN :postIds AND pl.user.id = :userId")
    fun findLikedPostIds(@Param("postIds") postIds:List<Long>, @Param("userId") userId:Long):List<Long>

    @Query("Select pl.post.id AS postId, COUNT(pl.id) AS count FROM PostLike pl WHERE pl.post.id IN :postIds GROUP BY pl.post.id")
    fun countGroupByPostIdIn(@Param("postIds") postIds:List<Long>): List<PostCountDto>
}