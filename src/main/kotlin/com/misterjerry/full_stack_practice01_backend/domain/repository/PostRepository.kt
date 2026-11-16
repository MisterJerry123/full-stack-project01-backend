package com.misterjerry.full_stack_practice01_backend.domain.repository

import com.misterjerry.full_stack_practice01_backend.data.entity.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long>{
    @EntityGraph(attributePaths = ["author"])
    override fun findAll(pageable: Pageable): Page<Post>


}