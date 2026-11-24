package com.example.blog.controller;

import com.example.blog.auth.LoginRequired;
import com.example.blog.common.ApiResponse;
import com.example.blog.service.ImageStorageService;
import com.example.blog.vo.ImageUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "图片模块", description = "先上传图片，再将返回路径写入文章封面或正文")
public class ImagesController {

    private final ImageStorageService imageStorageService;

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "上传图片", description = "登录后上传 JPEG、PNG、WebP，最大 5 MB；返回地址公开可读")
    @PostMapping(value = "/uploads/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ImageUploadVO> upload(@RequestPart("file") MultipartFile file) {
        return ApiResponse.created(imageStorageService.upload(file));
    }

    @Operation(summary = "读取图片", description = "无需登录，返回图片文件；图片不随文章状态或软删除改变可见性")
    @GetMapping("/images/{year}/{month}/{name}")
    public ResponseEntity<Resource> get(@PathVariable String year, @PathVariable String month,
            @PathVariable String name, HttpServletRequest request) {
        var image = imageStorageService.load(year, month, name);
        return ResponseEntity.ok()
                .contentType(image.mediaType())
                .contentLength(image.size())
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .header("X-Content-Type-Options", "nosniff")
                .body("HEAD".equals(request.getMethod()) ? null : image.resource());
    }
}
