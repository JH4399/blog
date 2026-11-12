package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章标签关联视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.PostTags}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "文章标签关联视图对象")
public class PostTagsVO {

    /** 文章 ID。 */
    @Schema(description = "文章 ID")
    private Long postId;

    /** 标签 ID。 */
    @Schema(description = "标签 ID")
    private Long tagId;
}
