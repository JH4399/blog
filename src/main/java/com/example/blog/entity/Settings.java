package com.example.blog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 系统配置实体，对应 BLOG.settings 表。
 */
@Getter
@Setter
@Schema(description = "系统配置")
public class Settings {

    /** 配置 ID，数据库自增。 */
    @Schema(description = "配置 ID")
    private Long id;

    /** 配置键，对应 config_key，必填，最长 100 个字符。 */
    @Schema(description = "配置键", maxLength = 100)
    private String configKey;

    /** 配置值，对应 config_value，数据库类型为 CLOB，可空。 */
    @Schema(description = "配置值")
    private String configValue;

    /** 配置说明，对应 remark，可空，最长 255 个字符。 */
    @Schema(description = "配置说明", maxLength = 255)
    private String remark;

    /** 更新时间，对应 updated_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
