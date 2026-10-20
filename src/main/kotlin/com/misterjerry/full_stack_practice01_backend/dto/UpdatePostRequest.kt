package com.misterjerry.full_stack_practice01_backend.dto

import jakarta.validation.constraints.NotBlank

data class UpdatePostRequest(
    @field:NotBlank(message = "제목은 필수 입력값입니다.")
    val title: String,
    val content: String,
    val imageUrl: String? = null
)