package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkListQuery extends ListQuery {
    @Min(0)
    @Max(1)
    @Schema(description = "友链状态：0隐藏、1显示；省略时不按状态筛选")
    private Integer status;
}
