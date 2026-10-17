package com.misterjerry.full_stack_practice01_backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpRequest (
    @field:NotBlank(message = "이메일은 필수 입력값입니다.")
    @field:Email(message = "이메일 형식이 맞지 않습니다.")
    val email:String,

    @field:NotBlank(message = "비밀번호는 필수 입력값입니다.")
    val password:String,
    @field:NotBlank(message = "이름은 필수 입력값입니다.")
    val name: String
)