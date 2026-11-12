package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 分类传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Categories}，字段独立声明。
 * 新增时不传 ID，修改时以路径 ID 定位记录；请求体 ID 可省略，传入时必须与路径一致。
 */
@Getter
@Setter
@Schema(description = "分类传输对象")
public class CategoriesDTO {

    /** 分类 ID，新增时不传，修改时可省略。 */
    @Schema(description = "分类 ID")
    @Positive(message = "分类 ID 必须大于 0")
    private Long id;

    /** 分类名称，最长 50 个字符。 */
    @Schema(description = "分类名称", maxLength = 50)
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过 50 个字符")
    private String name;

    /** URL 别名，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    @NotBlank(message = "URL 别名不能为空")
    @Size(max = 50, message = "URL 别名不能超过 50 个字符")
    private String slug;

    /** 分类描述，最长 255 个字符。 */
    @Schema(description = "分类描述", maxLength = 255)
    @Size(max = 255, message = "分类描述不能超过 255 个字符")
    private String description;

    /** 排序值。 */
    @Schema(description = "排序值")
    private Integer sort;
}
