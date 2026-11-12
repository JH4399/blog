package com.example.blog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文章实体，对应 BLOG.posts 表。
 */
@Getter
@Setter
@Schema(description = "文章")
public class Posts {

    /** 文章 ID，数据库自增。 */
    @Schema(description = "文章 ID")
    private Long id;

    /** 作者 ID，对应 user_id，必填，关联 users.id。 */
    @Schema(description = "作者 ID")
    private Long userId;

    /** 分类 ID，对应 category_id，可空，关联 categories.id。 */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 标题，对应 title，必填，最长 200 个字符。 */
    @Schema(description = "标题", maxLength = 200)
    private String title;

    /** URL 别名，对应 slug，必填，最长 200 个字符。 */
    @Schema(description = "URL 别名", maxLength = 200)
    private String slug;

    /** 摘要，对应 summary，可空，最长 500 个字符。 */
    @Schema(description = "摘要", maxLength = 500)
    private String summary;

    /** 正文，对应 content，数据库类型为 CLOB，必填，支持 Markdown 或 HTML。 */
    @Schema(description = "正文（Markdown/HTML）")
    private String content;

    /** 封面图 URL，对应 cover_image，可空，最长 255 个字符。 */
    @Schema(description = "封面图 URL", maxLength = 255)
    private String coverImage;

    /** 状态，对应 status，必填：0 草稿，1 已发布，2 私密；数据库默认值为 0。 */
    @Schema(description = "状态：0 草稿，1 已发布，2 私密")
    private Integer status;

    /** 是否置顶，对应 is_top，必填：0 否，1 是；数据库默认值为 0。 */
    @Schema(description = "是否置顶：0 否，1 是")
    private Integer isTop;

    /** 浏览量，对应 view_count，非空，数据库默认值为 0。 */
    @Schema(description = "浏览量")
    private Integer viewCount;

    /** 点赞数，对应 like_count，非空，数据库默认值为 0。 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 评论数，对应 comment_count，非空，数据库默认值为 0。 */
    @Schema(description = "评论数")
    private Integer commentCount;

    /** 发布时间，对应 published_at，可空。 */
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间，对应 updated_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /** 软删除时间，对应 deleted_at，可空；为空表示未删除。 */
    @Schema(description = "软删除时间，为空表示未删除")
    private LocalDateTime deletedAt;
}
