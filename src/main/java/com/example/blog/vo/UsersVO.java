package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Users}，字段独立声明。
 * 不包含密码及密码摘要。
 */
@Getter
@Setter
@Schema(description = "用户视图对象")
public class UsersVO {

    /** 用户 ID，由数据库生成。 */
    @Schema(description = "用户 ID")
    private Long id;

    /** 用户名，最长 50 个字符。 */
    @Schema(description = "用户名", maxLength = 50)
    private String username;

    /** 昵称，最长 50 个字符。 */
    @Schema(description = "昵称", maxLength = 50)
    private String nickname;

    /** 邮箱，最长 100 个字符。 */
    @Schema(description = "邮箱", maxLength = 100)
    private String email;

    /** 头像 URL，最长 255 个字符。 */
    @Schema(description = "头像 URL", maxLength = 255)
    private String avatar;

    /** 个人简介，最长 500 个字符。 */
    @Schema(description = "个人简介", maxLength = 500)
    private String bio;

    /** 角色：1 普通用户，2 管理员。 */
    @Schema(description = "角色：1 普通用户，2 管理员")
    private Integer role;

    /** 状态：0 禁用，1 正常。 */
    @Schema(description = "状态：0 禁用，1 正常")
    private Integer status;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
