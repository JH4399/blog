package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** Page numbers start at one; empty results still have one displayable page. */
public record PageResult<T>(
        @Schema(description = "当前页记录") List<T> records,
        @Schema(description = "匹配记录总数") long total,
        @Schema(description = "实际页码，从1开始；超出末页时回退", minimum = "1") int page,
        @Schema(description = "每页数量", minimum = "1", maximum = "100") int size,
        @Schema(description = "总页数；空结果为1", minimum = "1") long pages) {}
