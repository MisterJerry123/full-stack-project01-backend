package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.LoginRequest
import com.misterjerry.full_stack_practice01_backend.dto.LoginResponse
import com.misterjerry.full_stack_practice01_backend.dto.SignUpRequest
import com.misterjerry.full_stack_practice01_backend.dto.UserResponse
import com.misterjerry.full_stack_practice01_backend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
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
    @GetMapping("/asdf")
    fun automaticDeployTest():String{
        return "자동 배포 성공!!!"
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse>{
        val accessToken = userService.login(request.email,request.password)
        return ResponseEntity.ok(
            LoginResponse(accessToken)
        )
    }

    @PostMapping("/me")
    fun searchMyInfo(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<UserResponse>{
        val userId = jwt.subject?.toLong() ?: throw IllegalStateException("유효하지 않은 유저입니다.")
        val myInfo = userService.getMyInfo(userId = userId)
        return ResponseEntity.ok(myInfo)
    }
}