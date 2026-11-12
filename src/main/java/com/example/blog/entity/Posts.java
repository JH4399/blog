package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "\"posts\"", schema = "\"BLOG\"")
public class Posts {

    /** 文章 ID，数据库自增。 */
    @Schema(description = "文章 ID")
    @TableId(value = "\"id\"", type = IdType.AUTO)
    private Long id;

    /** 作者 ID，对应 user_id，必填，关联 users.id。 */
    @Schema(description = "作者 ID")
    @TableField("\"user_id\"")
    private Long userId;

    /** 分类 ID，对应 category_id，可空，关联 categories.id。 */
    @Schema(description = "分类 ID")
    @TableField("\"category_id\"")
    private Long categoryId;

    /** 标题，对应 title，必填，最长 200 个字符。 */
    @Schema(description = "标题", maxLength = 200)
    @TableField("\"title\"")
    private String title;

    /** URL 别名，对应 slug，必填，最长 200 个字符。 */
    @Schema(description = "URL 别名", maxLength = 200)
    @TableField("\"slug\"")
    private String slug;

    /** 摘要，对应 summary，可空，最长 500 个字符。 */
    @Schema(description = "摘要", maxLength = 500)
    @TableField("\"summary\"")
    private String summary;

    /** 正文，对应 content，数据库类型为 CLOB，必填，支持 Markdown 或 HTML。 */
    @Schema(description = "正文（Markdown/HTML）")
    @TableField("\"content\"")
    private String content;

    /** 封面图 URL，对应 cover_image，可空，最长 255 个字符。 */
    @Schema(description = "封面图 URL", maxLength = 255)
    @TableField("\"cover_image\"")
    private String coverImage;

    /** 状态，对应 status，必填：0 草稿，1 已发布，2 私密；数据库默认值为 0。 */
    @Schema(description = "状态：0 草稿，1 已发布，2 私密")
    @TableField("\"status\"")
    private Integer status;

    /** 是否置顶，对应 is_top，必填：0 否，1 是；数据库默认值为 0。 */
    @Schema(description = "是否置顶：0 否，1 是")
    @TableField("\"is_top\"")
    private Integer isTop;

    /** 浏览量，对应 view_count，非空，数据库默认值为 0。 */
    @Schema(description = "浏览量")
    @TableField("\"view_count\"")
    private Integer viewCount;

    /** 点赞数，对应 like_count，非空，数据库默认值为 0。 */
    @Schema(description = "点赞数")
    @TableField("\"like_count\"")
    private Integer likeCount;

    /** 评论数，对应 comment_count，非空，数据库默认值为 0。 */
    @Schema(description = "评论数")
    @TableField("\"comment_count\"")
    private Integer commentCount;

    /** 发布时间，对应 published_at，可空。 */
    @Schema(description = "发布时间")
    @TableField("\"published_at\"")
    private LocalDateTime publishedAt;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    @TableField("\"created_at\"")
    private LocalDateTime createdAt;

    /** 更新时间，对应 updated_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "更新时间")
    @TableField("\"updated_at\"")
    private LocalDateTime updatedAt;

    /** 软删除时间，对应 deleted_at，可空；为空表示未删除。 */
    @Schema(description = "软删除时间，为空表示未删除")
    @TableField("\"deleted_at\"")
    private LocalDateTime deletedAt;
}
