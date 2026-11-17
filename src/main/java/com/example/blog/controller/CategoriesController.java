package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.vo.PageResult;
import com.example.blog.dto.CategoriesDTO;
import com.example.blog.dto.SearchListQuery;
import com.example.blog.service.CategoryService;
import com.example.blog.vo.CategoriesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类模块", description = "分类模块")
public class CategoriesController {

    private final CategoryService categoryService;

    @Operation(summary = "新增分类", description = "名称和别名必填；别名重复返回 409")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoriesVO> create(@Valid @RequestBody CategoriesDTO request) {
        return ApiResponse.created(categoryService.create(request));
    }

    @Operation(summary = "修改分类", description = "完整替换分类的可编辑字段；分类不存在返回 404")
    @PutMapping("/{id}")
    public ApiResponse<CategoriesVO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody CategoriesDTO request) {
        return ApiResponse.success(categoryService.update(id, request));
    }

    @Operation(summary = "删除分类", description = "分类不存在返回 404；存在外键引用时返回 409")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id) {
        categoryService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "查询分类列表", description = "分页查询，默认page=1、size=10；返回records、total、page、size、pages")
    @GetMapping(produces = "application/json")
    public ApiResponse<PageResult<CategoriesVO>> list(@Valid @ParameterObject @ModelAttribute SearchListQuery query) {
        return ApiResponse.success(categoryService.list(query));
    }

    @Operation(summary = "查询分类详情", description = "分类不存在返回 404")
    @GetMapping("/{id}")
    public ApiResponse<CategoriesVO> get(@PathVariable @Positive Long id) {
        return ApiResponse.success(categoryService.get(id));
    }
}
