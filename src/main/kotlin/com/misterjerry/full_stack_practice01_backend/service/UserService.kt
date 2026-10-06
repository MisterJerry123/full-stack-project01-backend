package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.SignUpRequest
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository

class UserService(
    private val userRepository: UserRepository
) {
    fun signUp(request: SignUpRequest): Long{
        if(userRepository.existsByEmail(request.email)){
            throw IllegalArgumentException("이미 가입된 이메일 입니다.")
        }
        val user = User(
            email = request.email,
            password = request.password,
            name = request.name
        )
        return userRepository.save(user).id
    }

}