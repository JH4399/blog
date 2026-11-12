package com.example.blog.controller;

import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.common.ApiResponse;
import com.example.blog.dto.PostsDTO;
import com.example.blog.service.PostService;
import com.example.blog.vo.PostsVO;
import com.example.blog.vo.UsersVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@LoginRequired
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "文章模块", description = "文章模块")
public class PostsController {

    private final PostService postService;

    @Operation(summary = "新增文章", description = "作者取自登录用户，返回新增文章详情")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostsVO> create(@Valid @RequestBody PostsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.created(postService.create(request, user));
    }

    @Operation(summary = "修改文章", description = "完整替换可编辑字段；仅作者或管理员可操作")
    @PutMapping("/{id}")
    public ApiResponse<PostsVO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody PostsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.update(id, request, user));
    }

    @Operation(summary = "删除文章", description = "软删除；仅作者或管理员可操作")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        postService.delete(id, user);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "查询文章列表", description = "普通用户查询本人文章，管理员查询全部；排除已删除文章，按置顶、创建时间、ID 降序")
    @GetMapping
    public ApiResponse<List<PostsVO>> list(@RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.list(user));
    }

    @Operation(summary = "查询文章详情", description = "仅作者或管理员可查看；不存在或已删除返回 404")
    @GetMapping("/{id}")
    public ApiResponse<PostsVO> get(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postService.get(id, user));
    }
}
