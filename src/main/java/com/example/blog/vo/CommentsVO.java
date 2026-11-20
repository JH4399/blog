package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 评论视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Comments}，字段独立声明。
 * 不包含评论邮箱、IP 地址和浏览器 UA。
 */
@Getter
@Setter
@Schema(description = "评论视图对象")
public class CommentsVO {

    /** 评论 ID，由数据库生成。 */
    @Schema(description = "评论 ID")
    private Long id;

    /** 文章 ID。 */
    @Schema(description = "文章 ID")
    private Long postId;

    /** 登录用户 ID，游客评论时可为空。 */
    @Schema(description = "登录用户 ID，游客评论时可为空")
    private Long userId;

    /** 父评论 ID，顶级评论为空。 */
    @Schema(description = "父评论 ID，顶级评论为空")
    private Long parentId;

    /** 昵称（游客评论），最长 50 个字符。 */
    @Schema(description = "昵称（游客评论）", maxLength = 50)
    private String nickname;

    /** 个人网站，最长 255 个字符。 */
    @Schema(description = "个人网站", maxLength = 255)
    private String website;

    /** 评论内容。 */
    @Schema(description = "评论内容")
    private String content;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
