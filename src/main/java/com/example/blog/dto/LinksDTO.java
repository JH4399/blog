package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

/**
 * 友情链接传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Links}，字段独立声明。
 * 新增时不传 ID，修改时以路径 ID 定位记录；请求体 ID 可省略，传入时必须与路径一致。
 */
@Getter
@Setter
@Schema(description = "友情链接传输对象")
public class LinksDTO {

    /** 友情链接 ID，新增时不传，修改时可省略。 */
    @Schema(description = "友情链接 ID")
    @Positive(message = "友情链接 ID 必须大于 0")
    private Long id;

    /** 站点名称，最长 50 个字符。 */
    @Schema(description = "站点名称", maxLength = 50)
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 50, message = "站点名称不能超过 50 个字符")
    private String name;

    /** 站点 URL，最长 255 个字符。 */
    @Schema(description = "站点 URL", maxLength = 255)
    @NotBlank(message = "站点 URL 不能为空")
    @Size(max = 255, message = "站点 URL 不能超过 255 个字符")
    @URL(regexp = "(?i)https?://.*", message = "站点 URL 必须为有效的 HTTP 或 HTTPS 地址")
    private String url;

    /** 站点 Logo，最长 255 个字符。 */
    @Schema(description = "站点 Logo", maxLength = 255)
    @Size(max = 255, message = "站点 Logo 不能超过 255 个字符")
    private String logo;

    /** 站点描述，最长 255 个字符。 */
    @Schema(description = "站点描述", maxLength = 255)
    @Size(max = 255, message = "站点描述不能超过 255 个字符")
    private String description;

    /** 排序值。 */
    @Schema(description = "排序值")
    private Integer sort;

    /** 状态：0 隐藏，1 显示。 */
    @Schema(description = "状态：0 隐藏，1 显示")
    @Min(value = 0, message = "状态只能为 0 或 1")
    @Max(value = 1, message = "状态只能为 0 或 1")
    private Integer status;
}
