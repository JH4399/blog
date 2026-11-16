package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 评论新增参数。
 * 对应 {@link com.example.blog.entity.Comments}，字段独立声明。
 * 不允许指定 ID；当前接口要求登录，昵称省略时取登录用户昵称或用户名。
 * userId 应由服务端根据登录状态确定，status 由审核接口维护。
 */
@Getter
@Setter
@Schema(description = "评论传输对象")
public class CommentsDTO {

    /** 评论 ID，新增时不传。 */
    @Schema(description = "评论 ID，由数据库生成，请勿传入")
    @Null(message = "新增评论时不能指定 ID")
    private Long id;

    /** 文章 ID。 */
    @Schema(description = "文章 ID")
    @NotNull(message = "文章 ID 不能为空")
    @Positive(message = "文章 ID 必须为正整数")
    private Long postId;

    /** 登录用户 ID，游客评论时可为空。 */
    @Schema(description = "登录用户 ID，可省略；传入时必须与登录用户一致")
    @Positive(message = "用户 ID 必须为正整数")
    private Long userId;

    /** 父评论 ID，顶级评论为空。 */
    @Schema(description = "父评论 ID，顶级评论为空")
    @Positive(message = "父评论 ID 必须为正整数")
    private Long parentId;

    /** 昵称（游客评论），最长 50 个字符。 */
    @Schema(description = "昵称（游客评论）", maxLength = 50)
    @Size(max = 50, message = "昵称最多 50 个字符")
    private String nickname;

    /** 邮箱，最长 100 个字符。 */
    @Schema(description = "邮箱", maxLength = 100)
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱最多 100 个字符")
    private String email;

    /** 个人网站，最长 255 个字符。 */
    @Schema(description = "个人网站", maxLength = 255)
    @Size(max = 255, message = "个人网站最多 255 个字符")
    private String website;

    /** 评论内容。 */
    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 审核状态：0 待审核，1 已通过，2 已拒绝；由审核接口维护。 */
    @Schema(description = "状态由服务端设置为待审核，请勿传入")
    @Null(message = "审核状态由服务端维护")
    private Integer status;
}
