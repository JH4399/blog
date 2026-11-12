package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 登录凭据，不接受客户端指定用户 ID、角色或状态。 */
@Getter
@Setter
@Schema(description = "用户登录参数")
public class UserLoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名不能超过 50 个字符")
    @Pattern(regexp = "\\S+", message = "用户名不能包含空白字符")
    @Schema(description = "用户名", maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    // 登录只验证已有密码，不重新施加注册时的最小长度策略。
    @NotBlank(message = "密码不能为空")
    @Size(max = 72, message = "密码不能超过 72 个字符")
    @Schema(description = "原始密码，UTF-8 编码最多 72 字节", maxLength = 72,
            accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
