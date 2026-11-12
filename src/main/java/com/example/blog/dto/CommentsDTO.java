package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 评论传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Comments}，字段独立声明。
 * 新增时不传 ID，修改时使用 ID 定位记录；字段校验由具体接口负责。
 * userId 应由服务端根据登录状态确定，status 由审核接口维护。
 */
@Getter
@Setter
@Schema(description = "评论传输对象")
public class CommentsDTO {

    /** 评论 ID，新增时不传，修改时用于定位记录。 */
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

    /** 邮箱，最长 100 个字符。 */
    @Schema(description = "邮箱", maxLength = 100)
    private String email;

    /** 个人网站，最长 255 个字符。 */
    @Schema(description = "个人网站", maxLength = 255)
    private String website;

    /** 评论内容。 */
    @Schema(description = "评论内容")
    private String content;

    /** 审核状态：0 待审核，1 已通过，2 已拒绝；由审核接口维护。 */
    @Schema(description = "状态：0 待审核，1 已通过，2 已拒绝")
    private Integer status;
}
