package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 分类、标签、配置共用搜索条件；其他资源在此基础上扩展。 */
@Getter
@Setter
public class SearchListQuery extends ListQuery {
    @Size(max = 200, message = "搜索词不能超过200字符")
    @Schema(description = "搜索词，按字面量搜索，最多200字符")
    private String q = "";
}
