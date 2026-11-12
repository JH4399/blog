package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Users}，字段独立声明。
 * 新增时不传 ID，修改时使用 ID 定位记录；字段校验由具体接口负责。
 * role、status 用于后台用户管理，公开注册及个人资料接口应限制可修改字段。
 */
@Getter
@Setter
@Schema(description = "用户传输对象")
public class UsersDTO {

    /** 用户 ID，新增时不传，修改时用于定位记录。 */
    @Schema(description = "用户 ID")
    private Long id;

    /** 用户名，最长 50 个字符。 */
    @Schema(description = "用户名", maxLength = 50)
    private String username;

    /** 原始密码，服务端加密后保存；不得将客户端输入直接作为数据库密码摘要。 */
    @Schema(description = "密码（原文，由服务端加密后保存）", maxLength = 255, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

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

    /** 角色：1 普通用户，2 管理员；仅用于后台授权管理。 */
    @Schema(description = "角色：1 普通用户，2 管理员")
    private Integer role;

    /** 状态：0 禁用，1 正常；仅用于后台授权管理。 */
    @Schema(description = "状态：0 禁用，1 正常")
    private Integer status;
}
