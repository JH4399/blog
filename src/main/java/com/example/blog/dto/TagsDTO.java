package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Tags}，字段独立声明。
 * 新增时不传 ID，修改时以路径 ID 定位记录；请求体 ID 可省略，传入时必须与路径一致。
 */
@Getter
@Setter
@Schema(description = "标签传输对象")
public class TagsDTO {

    /** 标签 ID，新增时不传，修改时可省略。 */
    @Schema(description = "标签 ID")
    @Positive(message = "标签 ID 必须大于 0")
    private Long id;

    /** 标签名称，最长 50 个字符。 */
    @Schema(description = "标签名称", maxLength = 50)
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称不能超过 50 个字符")
    private String name;

    /** URL 别名，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    @NotBlank(message = "URL 别名不能为空")
    @Size(max = 50, message = "URL 别名不能超过 50 个字符")
    private String slug;
}
