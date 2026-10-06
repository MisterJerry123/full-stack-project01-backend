package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.SignUpRequest
import com.misterjerry.full_stack_practice01_backend.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController //HTTP요청을 받는 클래스
@RequestMapping("api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signUp")
    fun signUp(
        @RequestBody request: SignUpRequest
    ): Long{
        return userService.signUp(request)
    }

    @GetMapping("/test123")
    fun test(): String {
        return "ABC"
    }
}