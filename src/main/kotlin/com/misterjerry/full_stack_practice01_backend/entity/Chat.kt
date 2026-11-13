package com.misterjerry.full_stack_practice01_backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import java.time.LocalDateTime
import java.time.ZoneId

@Entity
class Chat (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long=0,

    @ManyToOne
    val sender:User,

    @ManyToOne
    val chattingRoom: ChattingRoom,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
){
    @PrePersist
    fun onCreated(){
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    }
}