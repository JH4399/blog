package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户分页条目，不含密码")
public class UserListVO extends UsersVO {
    @Schema(description = "该用户全部未删除文章的数量")
    private Long postCount;
}
