package com.misterjerry.full_stack_practice01_backend.domain.repository

import com.misterjerry.full_stack_practice01_backend.data.entity.ChattingRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChattingRoomRepository: JpaRepository<ChattingRoom, Long> {
    fun findByUser1IdOrUser2Id(user1Id:Long, user2Id: Long): List<ChattingRoom>

    @Query("""
        SELECT cr FROM ChattingRoom cr 
        WHERE (cr.user1.id = :user1Id AND cr.user2.id = :user2Id)
           OR (cr.user1.id = :user2Id AND cr.user2.id = :user1Id)
    """)
    fun findRoomBetweenUsers(user1Id: Long, user2Id: Long): ChattingRoom?

    @Query("""
        SELECT cr FROM ChattingRoom cr
        WHERE cr.user1.id=:userId OR cr.user2.id=:userId
        ORDER BY COALESCE(cr.lastMessageCreatedAt,cr.createdAt) DESC
    """)
    fun findByUserId(userId: Long):List<ChattingRoom>


    @Query("""
        SELECT CASE WHEN COUNT(cr)>0 THEN true ELSE false END
        FROM ChattingRoom cr
        WHERE cr.id=:roomId AND (cr.user1.id=:userId OR cr.user2.id=:userId)
    """)
    fun existsParticipant(
        @Param("roomId") roomId:Long,
        @Param("userId") userId: Long
    ): Boolean


}