package com.misterjerry.full_stack_practice01_backend.core.exception

data class ErrorResponse(
    val status: Int,
    val message: String
)
