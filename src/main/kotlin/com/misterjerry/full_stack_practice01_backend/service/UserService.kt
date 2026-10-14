package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.LoginRequest
import com.misterjerry.full_stack_practice01_backend.dto.SignUpRequest
import com.misterjerry.full_stack_practice01_backend.dto.UserResponse
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    fun signUp(request: SignUpRequest): Long {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 가입된 이메일 입니다.")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
            ?: throw IllegalArgumentException("비밀번호 인코딩 실패")
        val user = User(
            email = request.email,
            password = encodedPassword,
            name = request.name
        )
        return userRepository.save(user).id
    }

    fun login(email: String, password: String): String {
        val user = userRepository.findByEmail(email) ?: throw ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "이메일 또는 비밀번호가 올바르지 않습니다."
        )
        if (!passwordEncoder.matches(password, user.password)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "이메일 또는 비밀번호가 올바르지 않습니다."
            )
        }
        return jwtService.generateAccessToken(user.id)
    }

    fun getMyInfo(userId: Long): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.") }
        return UserResponse(user.id, user.name, user.email)
    }


}