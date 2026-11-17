package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Administrator-editable profile fields. Username and credentials are intentionally excluded. */
@Getter
@Setter
@Schema(description = "管理员修改用户资料、角色和状态")
public class AdminUserUpdateDTO {
    @Size(max = 50, message = "昵称不能超过 50 个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    private String email;

    @Size(max = 255, message = "头像地址不能超过 255 个字符")
    private String avatar;

    @Size(max = 500, message = "简介不能超过 500 个字符")
    private String bio;

    @NotNull(message = "角色不能为空")
    @Min(value = 1, message = "角色只能为 1 或 2")
    @Max(value = 2, message = "角色只能为 1 或 2")
    private Integer role;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为 0 或 1")
    @Max(value = 1, message = "状态只能为 0 或 1")
    private Integer status;
}
