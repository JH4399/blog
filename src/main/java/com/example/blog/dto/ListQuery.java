package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

/** 公共分页和搜索参数；省略时使用默认值，空值和非法值由 Spring MVC 校验。 */
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

    @Size(max = 200, message = "搜索词不能超过200字符")
    @Schema(description = "搜索词，按字面量搜索，最多200字符")
    private String q = "";

    /** SQL LIKE 的绑定值，保持大小写不敏感，并按字面量搜索通配符。 */
    public String searchPattern() {
        if (q == null || q.isBlank()) return null;
        return "%" + q.trim().toLowerCase(Locale.ROOT)
                .replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }
}
