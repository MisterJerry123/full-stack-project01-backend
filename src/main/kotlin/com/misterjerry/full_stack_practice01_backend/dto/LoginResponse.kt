package com.misterjerry.full_stack_practice01_backend.dto

data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)