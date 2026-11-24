package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListQuery extends ListQuery {
    @Min(0)
    @Max(2)
    @Schema(description = "文章状态：0草稿、1已发布、2私密")
    private Integer status;

    @Positive
    @Schema(description = "分类ID")
    private Long categoryId;

    @Positive
    @Schema(description = "标签ID")
    private Long tagId;

    @Positive
    @Schema(description = "作者ID，普通用户仅能查询本人文章及他人已发布文章")
    private Long authorId;

    @Pattern(regexp = "newest|views|likes")
    @Schema(description = "分页排序：newest创建时间、views浏览量、likes点赞数，均降序；省略时优先置顶")
    private String sort;
}
