package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 系统配置视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Settings}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "系统配置视图对象")
public class SettingsVO {

    /** 配置 ID，由数据库生成。 */
    @Schema(description = "配置 ID")
    private Long id;

    /** 配置键，最长 100 个字符。 */
    @Schema(description = "配置键", maxLength = 100)
    private String configKey;

    /** 配置值。 */
    @Schema(description = "配置值")
    private String configValue;

    /** 配置说明，最长 255 个字符。 */
    @Schema(description = "配置说明", maxLength = 255)
    private String remark;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
