package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.dto.PostCountDto
import com.misterjerry.full_stack_practice01_backend.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository: JpaRepository<Comment,Long> {

    fun findAllByPostId(postId:Long):List<Comment>
    fun countByPostId(postId: Long): Long
    fun deleteAllByPostId(postId: Long)

    @Query("SELECT c.post.id AS postId, COUNT(c.id) AS count FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    fun countGroupByPostIdIn(@Param("postIds") postIds:List<Long>):List<PostCountDto>
}