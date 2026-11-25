package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户修改本人资料的请求参数。
 * 用户 ID 由路径传入，用户名和密码不支持通过此请求修改。
 */
@Getter
@Setter
@Schema(description = "本人资料修改参数，不支持修改角色和状态")
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
}
