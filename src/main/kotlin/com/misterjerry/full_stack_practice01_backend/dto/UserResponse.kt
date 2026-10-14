package com.misterjerry.full_stack_practice01_backend.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String
)