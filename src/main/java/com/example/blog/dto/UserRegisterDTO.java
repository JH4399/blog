package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 公开注册请求。角色、状态、主键及时间由服务端生成。
 */
@Getter
@Setter
@Schema(description = "用户注册参数")
public class UserRegisterDTO {

    /** 用户名，不能为空且不能包含空白字符。 */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过 50 个字符")
    @Pattern(regexp = "\\S+", message = "用户名不能包含空白字符")
    @Schema(description = "用户名", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 原始密码，8 至 72 个字符，UTF-8 编码不得超过 BCrypt 的 72 字节上限。 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度必须为 8 至 72 个字符")
    @Schema(description = "原始密码，UTF-8 编码最多 72 字节", minLength = 8, maxLength = 72,
            accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /** 昵称，可不填。 */
    @Size(max = 50, message = "昵称不能超过 50 个字符")
    @Schema(description = "昵称", maxLength = 50)
    private String nickname;

    /** 邮箱，可不填；填写时必须为有效邮箱地址。 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    @Schema(description = "邮箱", maxLength = 100)
    private String email;

    /** 头像 URL，可不填。 */
    @Size(max = 255, message = "头像 URL 不能超过 255 个字符")
    @Schema(description = "头像 URL", maxLength = 255)
    private String avatar;

    /** 个人简介，可不填。 */
    @Size(max = 500, message = "个人简介不能超过 500 个字符")
    @Schema(description = "个人简介", maxLength = 500)
    private String bio;
}
