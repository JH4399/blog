package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.vo.PageResult;
import com.example.blog.dto.LinksDTO;
import com.example.blog.dto.LinkListQuery;
import com.example.blog.service.LinkService;
import com.example.blog.vo.LinksVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
@Tag(name = "友情链接模块", description = "友情链接增删改查")
public class LinksController {

    private final LinkService linkService;

    @Operation(summary = "新增友情链接", description = "名称和 URL 必填；默认排序为 0，状态为显示")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LinksVO> create(@Valid @RequestBody LinksDTO request) {
        return ApiResponse.created(linkService.create(request));
    }

    @Operation(summary = "修改友情链接", description = "完整替换可编辑字段；不存在返回 404")
    @PutMapping("/{id}")
    public ApiResponse<LinksVO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody LinksDTO request) {
        return ApiResponse.success(linkService.update(id, request));
    }

    @Operation(summary = "查询友情链接列表", description = "分页查询，默认page=1、size=10；返回records、total、page、size、pages")
    @GetMapping(produces = "application/json")
    public ApiResponse<PageResult<LinksVO>> list(@Valid @ParameterObject @ModelAttribute LinkListQuery query) {
        return ApiResponse.success(linkService.list(query));
    }

    @Operation(summary = "查询友情链接详情", description = "不存在返回 404")
    @GetMapping("/{id}")
    public ApiResponse<LinksVO> get(@PathVariable @Positive Long id) {
        return ApiResponse.success(linkService.get(id));
    }

    @Operation(summary = "删除友情链接", description = "不存在返回 404；存在外键引用时返回 409")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable @Positive Long id) {
        linkService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
