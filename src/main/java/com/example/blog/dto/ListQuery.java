package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/** 公共分页参数；省略时使用默认值，空值和非法值由 Spring MVC 校验。 */
@Getter
@Setter
public class ListQuery {
    @Min(value = 1, message = "页码不能小于1")
    @Max(value = 1000000, message = "页码不能超过1000000")
    @Schema(description = "页码，从1开始，默认1", defaultValue = "1", example = "1")
    private int page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    @Schema(description = "每页数量，默认10", defaultValue = "10", example = "10")
    private int size = 10;
}
