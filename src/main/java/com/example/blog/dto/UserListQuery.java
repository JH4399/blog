package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListQuery extends SearchListQuery {
    @Min(0)
    @Max(1)
    @Schema(description = "用户状态：0禁用、1正常")
    private Integer status;

    @Min(1)
    @Max(2)
    @Schema(description = "用户角色：1普通用户、2管理员")
    private Integer role;
}
