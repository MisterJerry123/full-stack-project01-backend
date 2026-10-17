package com.misterjerry.full_stack_practice01_backend.dto

data class GetPostsResponse(
    val posts:List<GetPostResponse>,
    val totalPages:Int,
    val totalElements: Long,
    val currentPage:Int
)
