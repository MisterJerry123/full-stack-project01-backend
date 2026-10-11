package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.SignUpRequest
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signUp(request: SignUpRequest): Long{
        if(userRepository.existsByEmail(request.email)){
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

}