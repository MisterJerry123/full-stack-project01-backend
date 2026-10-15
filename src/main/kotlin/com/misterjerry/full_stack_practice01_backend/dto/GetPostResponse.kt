package com.misterjerry.full_stack_practice01_backend.dto

import com.misterjerry.full_stack_practice01_backend.entity.Post

data class GetPostResponse(
    val post: Post
)