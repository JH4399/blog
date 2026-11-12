package com.example.blog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 文章标签关联实体，对应 BLOG.post_tags 表。
 * 此表仅包含文章 ID 和标签 ID，没有独立的自增 ID 字段。
 */
@Getter
@Setter
@Schema(description = "文章标签关联")
public class PostTags {

    /** 文章 ID，对应 post_id，必填，关联 posts.id。 */
    @Schema(description = "文章 ID")
    private Long postId;

    /** 标签 ID，对应 tag_id，必填，关联 tags.id。 */
    @Schema(description = "标签 ID")
    private Long tagId;
}
