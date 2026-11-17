package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 标准分页结果：保留请求页码，空结果总页数为0。 */
public record PageResult<T>(
        @Schema(description = "当前页记录") List<T> records,
        @Schema(description = "匹配记录总数") long total,
        @Schema(description = "请求页码，从1开始；超出末页时记录为空", minimum = "1") int page,
        @Schema(description = "每页数量", minimum = "1", maximum = "100") int size,
        @Schema(description = "总页数；空结果为0", minimum = "0") long pages) {}
