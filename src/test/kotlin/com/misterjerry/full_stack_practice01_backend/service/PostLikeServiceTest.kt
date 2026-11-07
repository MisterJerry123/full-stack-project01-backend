package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.entity.Post
import com.misterjerry.full_stack_practice01_backend.entity.User
import com.misterjerry.full_stack_practice01_backend.repository.PostLikeRepository
import com.misterjerry.full_stack_practice01_backend.repository.PostRepository
import com.misterjerry.full_stack_practice01_backend.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.atomics.AtomicInt
import kotlin.test.assertEquals

@SpringBootTest
class PostLikeServiceTest {
    @Autowired
    private lateinit var postLikeRepository: PostLikeRepository
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var postRepository: PostRepository
    @Autowired
    private lateinit var postLikeService: PostLikeService

    lateinit var postOwnerUser: User
    lateinit var postLikeUser: User
    lateinit var post: Post

    @BeforeEach
    fun setup(){
        postOwnerUser = User(
            email = "owner@test.com",
            password = "testtest",
            name = "게시글 작성자"
        )
        postLikeUser = User(
            email = "like@test.com",
            password = "testtest",
            name = "좋아요 누르는 사람"
        )
        userRepository.save(postOwnerUser)
        userRepository.save(postLikeUser)
        post = Post(
            title = "게시글 좋아요 동시성 테스트",
            content = "",
            author = postOwnerUser,
        )
        postRepository.save(post)
    }
    @AfterEach
    fun tearDown(){
        postLikeRepository.deleteAll()
        postRepository.delete(post)
        userRepository.delete(postLikeUser)
        userRepository.delete(postOwnerUser)
    }


    @Test
    fun`동시에 2번 좋아요를 누르면 1번만 등록되어야한다`(){
        val numberOfThread = 2
        val executorService = Executors.newFixedThreadPool(numberOfThread)
        val readyLatch = CountDownLatch(numberOfThread)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(numberOfThread)

        val success = AtomicInteger(0)
        val fail = AtomicInteger(0)

        for(i in 0 until numberOfThread){
            executorService.execute {
                readyLatch.countDown()
                try{
                    startLatch.await()
                    postLikeService.postLike(postLikeUser.id,post.id)
                    success.incrementAndGet()
                }catch (e: Exception){
                    fail.incrementAndGet()
                }finally {
                    doneLatch.countDown()
                }
            }
        }
        readyLatch.await()
        startLatch.countDown()
        doneLatch.await()

        assertThat(success.get()).isEqualTo(1)
        assertThat(fail.get()).isEqualTo(1)
        assertThat(postLikeRepository.countByPostId(post.id)).isEqualTo(1L)


    }

}