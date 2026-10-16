package com.misterjerry.full_stack_practice01_backend.dto

import com.misterjerry.full_stack_practice01_backend.entity.Post

data class GetPostsResponse(
    val posts:List<Post>,
    val totalPages:Int,
    val totalElements: Long,
    val currentPage:Int
)
