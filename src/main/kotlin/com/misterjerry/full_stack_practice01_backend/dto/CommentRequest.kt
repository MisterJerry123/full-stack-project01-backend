package com.misterjerry.full_stack_practice01_backend.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class CommentRequest(
    @field:NotBlank(message = "빈 댓글은 허용되지 않습니다.")
    val comment: String,
)