package com.misterjerry.full_stack_practice01_backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수 입력값입니다.")
    @field:Email(message = "이메일 형식에 맞지 않습니다.")
    val email: String,

    @field:NotBlank(message = "공백은 들어갈 수 없습니다.")
    val password: String,
)
