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
 * 用户实体，对应 BLOG.users 表。
 */
@Getter
@Setter
@Schema(description = "用户")
@TableName(value = "\"users\"", schema = "\"BLOG\"")
public class Users {

    /** 用户 ID，数据库自增。 */
    @Schema(description = "用户 ID")
    @TableId(value = "\"id\"", type = IdType.AUTO)
    private Long id;

    /** 用户名，对应 username，必填，最长 50 个字符。 */
    @Schema(description = "用户名", maxLength = 50)
    @TableField("\"username\"")
    private String username;

    /** 加密后的密码，对应 password，必填，最长 255 个字符。 */
    @Schema(description = "密码（加密后）", maxLength = 255)
    @TableField("\"password\"")
    private String password;

    /** 昵称，对应 nickname，可空，最长 50 个字符。 */
    @Schema(description = "昵称", maxLength = 50)
    @TableField("\"nickname\"")
    private String nickname;

    /** 邮箱，对应 email，可空，最长 100 个字符。 */
    @Schema(description = "邮箱", maxLength = 100)
    @TableField("\"email\"")
    private String email;

    /** 头像 URL，对应 avatar，可空，最长 255 个字符。 */
    @Schema(description = "头像 URL", maxLength = 255)
    @TableField("\"avatar\"")
    private String avatar;

    /** 个人简介，对应 bio，可空，最长 500 个字符。 */
    @Schema(description = "个人简介", maxLength = 500)
    @TableField("\"bio\"")
    private String bio;

    /** 角色，对应 role，必填：1 普通用户，2 管理员；数据库默认值为 1。 */
    @Schema(description = "角色：1 普通用户，2 管理员")
    @TableField("\"role\"")
    private Integer role;

    /** 状态，对应 status，必填：0 禁用，1 正常；数据库默认值为 1。 */
    @Schema(description = "状态：0 禁用，1 正常")
    @TableField("\"status\"")
    private Integer status;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    @TableField("\"created_at\"")
    private LocalDateTime createdAt;

    /** 更新时间，对应 updated_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "更新时间")
    @TableField("\"updated_at\"")
    private LocalDateTime updatedAt;
}
