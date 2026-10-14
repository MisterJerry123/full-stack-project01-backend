package com.misterjerry.full_stack_practice01_backend.repository

import com.misterjerry.full_stack_practice01_backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {

    fun existsByEmail(email:String): Boolean //Spring Data JPA가 알아서 쿼리를 만들어 줄 수 있게 명명
    //[동작] + By + [조건]으로 명명하고 By뒤에 조건이 엔티티 이름이랑 같아야함

    fun findByEmail(email: String): User?
    
}