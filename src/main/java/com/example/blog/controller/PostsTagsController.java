package com.example.blog.controller;

import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.common.ApiResponse;
import com.example.blog.dto.PostTagsDTO;
import com.example.blog.service.PostTagService;
import com.example.blog.vo.PostTagsVO;
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
@RequestMapping("/api/post-tags")
@RequiredArgsConstructor
@LoginRequired
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "文章标签关联模块", description = "仅文章作者或管理员可管理文章标签关联")
public class PostsTagsController {

    private final PostTagService postTagService;

    @Operation(summary = "绑定文章标签", description = "文章和标签必须存在；重复绑定返回 409")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostTagsVO> create(@Valid @RequestBody PostTagsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.created(postTagService.create(request, user));
    }

    @Operation(summary = "替换文章标签", description = "路径定位原关联，请求体指定新标签；postId 必须与路径一致")
    @PutMapping("/{postId}/{tagId}")
    public ApiResponse<PostTagsVO> update(@PathVariable @Positive Long postId,
            @PathVariable @Positive Long tagId, @Valid @RequestBody PostTagsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postTagService.update(postId, tagId, request, user));
    }

    @Operation(summary = "解绑文章标签", description = "只删除指定关联；关联不存在返回 404")
    @DeleteMapping("/{postId}/{tagId}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long postId, @PathVariable @Positive Long tagId,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        postTagService.delete(postId, tagId, user);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "查询文章标签关联列表", description = "postId 必填；按标签 ID 升序返回")
    @GetMapping
    public ApiResponse<List<PostTagsVO>> list(@RequestParam @Positive Long postId,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postTagService.list(postId, user));
    }

    @Operation(summary = "查询文章标签关联", description = "通过文章 ID 和标签 ID 定位关联；不存在返回 404")
    @GetMapping("/{postId}/{tagId}")
    public ApiResponse<PostTagsVO> get(@PathVariable @Positive Long postId, @PathVariable @Positive Long tagId,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(postTagService.get(postId, tagId, user));
    }
}
