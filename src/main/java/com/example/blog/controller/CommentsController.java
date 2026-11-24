package com.example.blog.controller;

import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.auth.TokenManager;
import com.example.blog.common.ApiResponse;
import com.example.blog.dto.CommentsDTO;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;
import com.example.blog.vo.CommentsVO;
import com.example.blog.vo.UsersVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "评论模块", description = "新增、删除和查询评论")
public class CommentsController {

    private final CommentService commentService;
    private final TokenManager tokenManager;
    private final UserService userService;

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "新增评论", description = "评论用户取自登录态，提交后立即可见；支持同一文章下的回复")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentsVO> create(@Valid @RequestBody CommentsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user,
            HttpServletRequest servletRequest) {
        return ApiResponse.created(commentService.create(request, user,
                servletRequest.getRemoteAddr(), servletRequest.getHeader("User-Agent")));
    }

    @LoginRequired
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "删除评论", description = "评论本人、文章作者或管理员可删除；存在回复时返回 409")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        commentService.delete(id, user);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "查询文章评论", description = "无需登录；游客可查看已发布文章的全部评论，可携带 Bearer Token；按创建时间、ID 升序")
    @GetMapping
    public ApiResponse<List<CommentsVO>> list(@RequestParam @Positive Long postId,
            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return ApiResponse.success(commentService.list(postId, resolveOptionalUser(request)));
    }

    @Operation(summary = "查询评论详情", description = "无需登录；可携带 Bearer Token，可见范围与文章评论列表一致")
    @GetMapping("/{id}")
    public ApiResponse<CommentsVO> get(@PathVariable @Positive Long id,
            HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        return ApiResponse.success(commentService.get(id, resolveOptionalUser(request)));
    }

    private UsersVO resolveOptionalUser(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorization == null ? null
                : userService.currentUser(tokenManager.resolveBearerToken(authorization));
    }
}
