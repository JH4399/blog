package com.example.blog.controller;

import com.example.blog.auth.LoginInterceptor;
import com.example.blog.auth.LoginRequired;
import com.example.blog.common.ApiResponse;
import com.example.blog.vo.PageResult;
import com.example.blog.dto.SearchListQuery;
import com.example.blog.dto.SettingsDTO;
import com.example.blog.service.SettingService;
import com.example.blog.vo.SettingsVO;
import com.example.blog.vo.UsersVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@LoginRequired
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "系统配置模块", description = "仅管理员可访问系统配置")
public class SettingsController {

    private final SettingService settingService;

    @Operation(summary = "新增配置", description = "配置键必填且唯一；重复返回 409")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SettingsVO> create(@Valid @RequestBody SettingsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.created(settingService.create(request, user));
    }

    @Operation(summary = "修改配置", description = "完整替换可编辑字段；配置不存在返回 404")
    @PutMapping("/{id}")
    public ApiResponse<SettingsVO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody SettingsDTO request,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(settingService.update(id, request, user));
    }

    @Operation(summary = "删除配置", description = "配置不存在返回 404")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        settingService.delete(id, user);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "查询配置列表", description = "分页查询，默认page=1、size=10；返回records、total、page、size、pages")
    @GetMapping(produces = "application/json")
    public ApiResponse<PageResult<SettingsVO>> list(@Valid @ParameterObject @ModelAttribute SearchListQuery query,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(settingService.list(query, user));
    }

    @Operation(summary = "查询配置详情", description = "配置不存在返回 404")
    @GetMapping("/{id}")
    public ApiResponse<SettingsVO> get(@PathVariable @Positive Long id,
            @RequestAttribute(LoginInterceptor.CURRENT_USER) UsersVO user) {
        return ApiResponse.success(settingService.get(id, user));
    }
}
