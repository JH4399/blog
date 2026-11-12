package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 标签视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Tags}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "标签视图对象")
public class TagsVO {

    /** 标签 ID，由数据库生成。 */
    @Schema(description = "标签 ID")
    private Long id;

    /** 标签名称，最长 50 个字符。 */
    @Schema(description = "标签名称", maxLength = 50)
    private String name;

    /** URL 别名，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    private String slug;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
