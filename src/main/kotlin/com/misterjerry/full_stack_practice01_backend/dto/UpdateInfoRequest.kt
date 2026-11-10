package com.misterjerry.full_stack_practice01_backend.dto

data class UpdateInfoRequest(
    val email: String ="",
    val password: String="",
    val name: String=""
)