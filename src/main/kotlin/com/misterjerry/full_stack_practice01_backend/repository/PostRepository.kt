package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long>{



}