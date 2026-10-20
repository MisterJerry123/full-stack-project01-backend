package com.misterjerry.full_stack_practice01_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig(
    @Value("\${file.upload-dir}")
    private val uploadDir: String
): WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // 클라이언트가 "/uploads/**" 주소로 요청하면,
        // 서버의 실제 디스크 "uploads/" 폴더에서 파일을 찾아 전달해 줍니다!
        val uploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString()
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(uploadPath)
    }
}