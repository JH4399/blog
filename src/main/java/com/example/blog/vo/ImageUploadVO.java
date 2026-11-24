package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "图片上传结果，url 可用于文章 coverImage 或正文图片引用")
public record ImageUploadVO(
        @Schema(description = "稳定的图片访问路径") String url,
        @Schema(description = "原始文件名，仅作展示") String originalName,
        @Schema(description = "文件大小，字节") long size,
        @Schema(description = "实际图片 MIME 类型") String contentType,
        @Schema(description = "图片宽度，像素") int width,
        @Schema(description = "图片高度，像素") int height) {
}
