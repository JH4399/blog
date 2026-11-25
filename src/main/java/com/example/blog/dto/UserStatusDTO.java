package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** 管理员修改用户状态的请求参数。 */
@Getter
@Setter
@Schema(description = "用户状态修改参数，仅管理员可使用")
public class UserStatusDTO {

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为 0 或 1")
    @Max(value = 1, message = "状态只能为 0 或 1")
    @Schema(description = "状态：0 禁用，1 正常")
    private Integer status;
}
