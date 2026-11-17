package com.example.blog.controller;

import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.common.ApiResponse;
import com.example.blog.vo.PageResult;
import com.example.blog.dto.AdminUserUpdateDTO;
import com.example.blog.dto.UserListQuery;
import com.example.blog.service.AdminUserService;
import com.example.blog.vo.UsersVO;
import com.example.blog.vo.UserListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@LoginRequired
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "后台用户管理", description = "仅管理员可查看全部用户和修改资料、角色及状态")
public class AdminUsersController {
    private final AdminUserService adminUserService;

    @GetMapping(produces = "application/json")
    @Operation(summary = "分页查询用户", description = "分页查询，默认page=1、size=10；返回records、total、page、size、pages")
    public ApiResponse<PageResult<UserListVO>> list(@Valid @ParameterObject @ModelAttribute UserListQuery query,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(adminUserService.list(query, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改用户", description = "完整提交可编辑字段；不允许禁用或降级当前账户，不修改用户名、密码和创建时间")
    public ApiResponse<UsersVO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody AdminUserUpdateDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(adminUserService.update(id, request, user));
    }
}
