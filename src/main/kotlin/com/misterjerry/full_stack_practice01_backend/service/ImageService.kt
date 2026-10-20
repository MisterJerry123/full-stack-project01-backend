package com.misterjerry.full_stack_practice01_backend.service

import com.misterjerry.full_stack_practice01_backend.dto.UploadImageResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Service
class ImageService(
    @Value("\${file.upload-dir}")
    private val uploadDir: String // "uploads/" 경로 주입
) {

    private val allowedExtensions = setOf("jpg", "jpeg", "png", "webp", "gif")

    fun uploadImage(file: MultipartFile): UploadImageResponse{
        if(file.isEmpty){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"빈 파일 입니다.")
        }
        val extension = file.originalFilename?.substringAfter(".")?.lowercase()
        if(extension !in allowedExtensions){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "허용하지 않는 파일 확장자 입니다.")
        }
        val fileName= "${UUID.randomUUID()}.${extension}"

        val uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize()
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath)
        }

        val targetFile = uploadPath.resolve(fileName).toFile()
        file.transferTo(targetFile)

        return UploadImageResponse(imageUrl = "/uploads/$fileName")
    }
}