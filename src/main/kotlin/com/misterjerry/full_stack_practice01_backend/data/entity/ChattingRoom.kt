package com.misterjerry.full_stack_practice01_backend.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import org.springframework.cglib.core.Local
import java.time.LocalDateTime
import java.time.ZoneId

@Entity
class ChattingRoom (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long=0,
    @ManyToOne(fetch = FetchType.LAZY)
    val user1: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val user2: User,
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column
    var lastMessage:String? = null,
    @Column
    var lastMessageCreatedAt: LocalDateTime?=null
){
    @PrePersist
    fun onCreate(){
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    }
}