package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文章视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Posts}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "文章视图对象")
public class PostsVO {

    /** 文章 ID，由数据库生成。 */
    @Schema(description = "文章 ID")
    private Long id;

    /** 作者 ID。 */
    @Schema(description = "作者 ID")
    private Long userId;

    /** 分类 ID。 */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 标题，最长 200 个字符。 */
    @Schema(description = "标题", maxLength = 200)
    private String title;

    /** URL 别名，最长 200 个字符。 */
    @Schema(description = "URL 别名", maxLength = 200)
    private String slug;

    /** 摘要，最长 500 个字符。 */
    @Schema(description = "摘要", maxLength = 500)
    private String summary;

    /** 正文（Markdown/HTML）。 */
    @Schema(description = "正文（Markdown/HTML）")
    private String content;

    /** 封面图 URL，最长 255 个字符。 */
    @Schema(description = "封面图 URL", maxLength = 255)
    private String coverImage;

    /** 状态：0 草稿，1 已发布，2 私密。 */
    @Schema(description = "状态：0 草稿，1 已发布，2 私密")
    private Integer status;

    /** 是否置顶：0 否，1 是。 */
    @Schema(description = "是否置顶：0 否，1 是")
    private Integer isTop;

    /** 浏览量。 */
    @Schema(description = "浏览量")
    private Integer viewCount;

    /** 点赞数。 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 评论数。 */
    @Schema(description = "评论数")
    private Integer commentCount;

    /** 发布时间。 */
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
