package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 管理员修改用户资料、角色和状态的请求参数。
 * 用户 ID 由路径传入，用户名和密码不支持通过此请求修改。
 */
@Getter
@Setter
@Schema(description = "用户修改参数，仅管理员可使用")
public class UsersDTO {

    /** 昵称，最长 50 个字符。 */
    @Size(max = 50, message = "昵称不能超过 50 个字符")
    @Schema(description = "昵称", maxLength = 50)
    private String nickname;

    /** 邮箱，最长 100 个字符。 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    @Schema(description = "邮箱", maxLength = 100)
    private String email;

    /** 头像 URL，最长 255 个字符。 */
    @Size(max = 255, message = "头像地址不能超过 255 个字符")
    @Schema(description = "头像 URL", maxLength = 255)
    private String avatar;

    /** 个人简介，最长 500 个字符。 */
    @Size(max = 500, message = "简介不能超过 500 个字符")
    @Schema(description = "个人简介", maxLength = 500)
    private String bio;

    /** 角色：1 普通用户，2 管理员；仅用于后台授权管理。 */
    @NotNull(message = "角色不能为空")
    @Min(value = 1, message = "角色只能为 1 或 2")
    @Max(value = 2, message = "角色只能为 1 或 2")
    @Schema(description = "角色：1 普通用户，2 管理员")
    private Integer role;

    /** 状态：0 禁用，1 正常；仅用于后台授权管理。 */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为 0 或 1")
    @Max(value = 1, message = "状态只能为 0 或 1")
    @Schema(description = "状态：0 禁用，1 正常")
    private Integer status;
}
