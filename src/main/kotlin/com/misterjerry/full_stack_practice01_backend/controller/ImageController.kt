package com.misterjerry.full_stack_practice01_backend.controller

import com.misterjerry.full_stack_practice01_backend.dto.UploadImageResponse
import com.misterjerry.full_stack_practice01_backend.service.ImageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/images")
class ImageController(
    private val imageService: ImageService
) {

    @Operation(
        summary = "게시글에 이미지 업로드",
        description = "게시글에 이미지를 업로드 합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<UploadImageResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.uploadImage(file))
    }
}