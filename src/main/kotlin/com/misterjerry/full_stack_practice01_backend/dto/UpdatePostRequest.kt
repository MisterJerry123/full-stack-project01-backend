package com.misterjerry.full_stack_practice01_backend.dto

import com.misterjerry.full_stack_practice01_backend.entity.Post

data class UpdatePostRequest(
    val title:String,
    val content:String
)