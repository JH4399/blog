package com.example.blog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 评论实体，对应 BLOG.comments 表。
 */
@Getter
@Setter
@Schema(description = "评论")
public class Comments {

    /** 评论 ID，数据库自增。 */
    @Schema(description = "评论 ID")
    private Long id;

    /** 文章 ID，对应 post_id，必填，关联 posts.id。 */
    @Schema(description = "文章 ID")
    private Long postId;

    /** 登录用户 ID，对应 user_id，可空；游客评论时为空，关联 users.id。 */
    @Schema(description = "登录用户 ID，游客评论时可为空")
    private Long userId;

    /** 父评论 ID，对应 parent_id，可空；顶级评论为 NULL，关联 comments.id。 */
    @Schema(description = "父评论 ID，顶级评论为空")
    private Long parentId;

    /** 评论昵称，对应 nickname，必填，最长 50 个字符，用于游客评论。 */
    @Schema(description = "昵称（游客评论）", maxLength = 50)
    private String nickname;

    /** 邮箱，对应 email，可空，最长 100 个字符。 */
    @Schema(description = "邮箱", maxLength = 100)
    private String email;

    /** 个人网站，对应 website，可空，最长 255 个字符。 */
    @Schema(description = "个人网站", maxLength = 255)
    private String website;

    /** 评论内容，对应 content，数据库类型为 CLOB，必填。 */
    @Schema(description = "评论内容")
    private String content;

    /** 状态，对应 status，必填：0 待审核，1 已通过，2 已拒绝；数据库默认值为 0。 */
    @Schema(description = "状态：0 待审核，1 已通过，2 已拒绝")
    private Integer status;

    /** IP 地址，对应 ip，可空，最长 45 个字符。 */
    @Schema(description = "IP 地址", maxLength = 45)
    private String ip;

    /** 浏览器 UA，对应 user_agent，可空，最长 255 个字符。 */
    @Schema(description = "浏览器 UA", maxLength = 255)
    private String userAgent;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
