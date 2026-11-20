package com.example.blog.controller;

import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.common.ApiResponse;
import com.example.blog.vo.PageResult;
import com.example.blog.dto.PostListQuery;
import com.example.blog.dto.PostsDTO;
import com.example.blog.service.PostService;
import com.example.blog.auth.TokenManager;
import com.example.blog.service.UserService;
import com.example.blog.vo.PostsVO;
import com.example.blog.vo.PostListVO;
import com.example.blog.vo.UsersVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "文章模块", description = "文章模块")
public class PostsController {

    private final PostService postService;
    private final TokenManager tokenManager;
    private final UserService userService;

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "新增文章", description = "作者取自登录用户，返回新增文章详情")
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostsVO> create(@Valid @RequestBody PostsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.created(postService.create(request, user));
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "修改文章", description = "完整替换可编辑字段；仅作者或管理员可操作")
    @PutMapping("/posts/{id}")
    public ApiResponse<PostsVO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody PostsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.update(id, request, user));
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "删除文章", description = "软删除；仅作者或管理员可操作")
    @DeleteMapping("/posts/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        postService.delete(id, user);
        return ApiResponse.success("删除成功", null);
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "查询文章列表", description = "普通用户可查看本人文章和他人已发布文章，管理员可查看全部；分页默认page=1、size=10")
    @GetMapping(value = "/posts", produces = "application/json")
    public ApiResponse<PageResult<PostListVO>> list(@Valid @ParameterObject @ModelAttribute PostListQuery query,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.list(query, user));
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/posts/summary")
    @Operation(summary = "全站统计", description = "仅管理员；返回全站统计与最近10篇文章，不受分页筛选影响")
    public ApiResponse<Map<String, Object>> summary(@RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.summary(user));
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "查询文章详情", description = "已发布文章登录后可查看，草稿和私密文章仅作者或管理员可查看；不存在或已删除返回 404")
    @GetMapping("/posts/{id}")
    public ApiResponse<PostsVO> get(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.get(id, user));
    }

    @Operation(summary = "公开文章详情", description = "无需登录；可携带 Bearer Token。"
            + "同一账号或匿名 IP 对同一文章每 24 小时计一次，作者本人不计数；HEAD 不计数")
    @GetMapping("/public/posts/{id}")
    public ApiResponse<PostsVO> read(@PathVariable @Positive Long id,
            HttpServletRequest request, HttpServletResponse response) {
        // 每次阅读均到达服务端，避免浏览器或共享缓存绕过计数及身份判断。
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long readerId = authorization == null ? null
                : userService.currentUser(tokenManager.resolveBearerToken(authorization)).getId();
        // 不直接信任客户端传入的 X-Forwarded-For / X-Real-IP。
        return ApiResponse.success(postService.read(id, readerId, request.getRemoteAddr(),
                "GET".equals(request.getMethod())));
    }
}
