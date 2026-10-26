package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment,Long> {

    fun findAllByPostId(postId:Long):List<Comment>
    fun countByPostId(postId: Long): Long
}