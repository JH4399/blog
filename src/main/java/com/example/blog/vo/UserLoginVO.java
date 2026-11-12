package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/** Token 仅在登录成功时返回，后续通过 Authorization 请求头发送。 */
@Schema(description = "用户登录结果")
public record UserLoginVO(
        @Schema(description = "随机访问令牌") String accessToken,
        @Schema(description = "认证类型", example = "Bearer") String tokenType,
        @Schema(description = "有效期，单位秒", example = "7200") long expiresIn,
        @Schema(description = "不含密码的用户资料") UsersVO user) {
}
