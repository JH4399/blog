package com.example.blog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 标签实体，对应 BLOG.tags 表。
 */
@Getter
@Setter
@Schema(description = "标签")
public class Tags {

    /** 标签 ID，数据库自增。 */
    @Schema(description = "标签 ID")
    private Long id;

    /** 标签名称，对应 name，必填，最长 50 个字符。 */
    @Schema(description = "标签名称", maxLength = 50)
    private String name;

    /** URL 别名，对应 slug，必填，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    private String slug;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
