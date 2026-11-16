package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统配置传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Settings}，字段独立声明。
 * 新增时不传 ID，修改时以路径 ID 定位记录；请求体 ID 可省略，传入时必须与路径一致。
 */
@Getter
@Setter
@Schema(description = "系统配置传输对象")
public class SettingsDTO {

    /** 配置 ID，新增时不传，修改时用于定位记录。 */
    @Schema(description = "配置 ID")
    @Positive(message = "配置 ID 必须大于 0")
    private Long id;

    /** 配置键，最长 100 个字符。 */
    @Schema(description = "配置键", maxLength = 100)
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键不能超过 100 个字符")
    private String configKey;

    /** 配置值。 */
    @Schema(description = "配置值")
    private String configValue;

    /** 配置说明，最长 255 个字符。 */
    @Schema(description = "配置说明", maxLength = 255)
    @Size(max = 255, message = "配置说明不能超过 255 个字符")
    private String remark;
}
