package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.TagsDTO;
import com.example.blog.service.TagService;
import com.example.blog.vo.TagsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "标签模块", description = "标签模块")
public class TagsController {

    private final TagService tagService;

    @Operation(summary = "新增标签", description = "名称和别名必填；别名重复返回 409")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TagsVO> create(@Valid @RequestBody TagsDTO request) {
        return ApiResponse.created(tagService.create(request));
    }

    @Operation(summary = "修改标签", description = "完整替换标签的可编辑字段；标签不存在返回 404")
    @PutMapping("/{id}")
    public ApiResponse<TagsVO> update(@PathVariable @Positive Long id, @Valid @RequestBody TagsDTO request) {
        return ApiResponse.success(tagService.update(id, request));
    }

    @Operation(summary = "删除标签", description = "标签不存在返回 404；存在外键引用时返回 409")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id) {
        tagService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "查询标签列表", description = "按 ID 升序返回全部标签")
    @GetMapping
    public ApiResponse<List<TagsVO>> list() {
        return ApiResponse.success(tagService.list());
    }

    @Operation(summary = "查询标签详情", description = "标签不存在返回 404")
    @GetMapping("/{id}")
    public ApiResponse<TagsVO> get(@PathVariable @Positive Long id) {
        return ApiResponse.success(tagService.get(id));
    }
}
