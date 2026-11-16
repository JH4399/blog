package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章标签关联传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.PostTags}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "文章标签关联传输对象")
public class PostTagsDTO {

    /** 文章 ID。 */
    @Schema(description = "文章 ID")
    @NotNull(message = "文章 ID 不能为空")
    @Positive(message = "文章 ID 必须大于 0")
    private Long postId;

    /** 标签 ID。 */
    @Schema(description = "标签 ID")
    @NotNull(message = "标签 ID 不能为空")
    @Positive(message = "标签 ID 必须大于 0")
    private Long tagId;
}
