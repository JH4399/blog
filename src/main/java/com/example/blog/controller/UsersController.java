package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.dto.UserLoginDTO;
import com.example.blog.dto.UserRegisterDTO;
import com.example.blog.service.UserService;
import com.example.blog.vo.UsersVO;
import com.example.blog.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口，负责接收和校验请求参数。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户登录", description = "用户登录模块")
public class UsersController {

    private final UserService userService;

    /**
     * 注册普通用户，返回不含密码的用户资料。
     *
     * @param request 注册参数
     * @return 注册成功的用户资料
     */
    @Operation(summary = "用户注册", description = "注册普通用户；用户名重复返回 409，参数不合法返回 400")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UsersVO> register(@Valid @RequestBody UserRegisterDTO request) {
        return ApiResponse.created(userService.register(request));
    }

    @Operation(summary = "用户登录", description = "返回 Bearer Token；凭据错误返回 401，账号禁用返回 403")
    @PostMapping("/login")
    public ApiResponse<UserLoginVO> login(@Valid @RequestBody UserLoginDTO request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return ApiResponse.success("登录成功", userService.login(request));
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "当前用户", description = "通过 Authorization: Bearer <Token> 查询当前登录用户")
    @GetMapping("/me")
    public ApiResponse<UsersVO> me(@RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(user);
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "退出登录", description = "撤销当前 Token，其他设备的登录态不受影响")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestAttribute(LoginInterceptor.CURRENT_TOKEN) String token) {
        userService.logout(token);
        return ApiResponse.success("退出登录成功", null);
    }

}
