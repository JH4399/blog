package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 分类视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Categories}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "分类视图对象")
public class CategoriesVO {

    /** 分类 ID，由数据库生成。 */
    @Schema(description = "分类 ID")
    private Long id;

    /** 分类名称，最长 50 个字符。 */
    @Schema(description = "分类名称", maxLength = 50)
    private String name;

    /** URL 别名，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    private String slug;

    /** 分类描述，最长 255 个字符。 */
    @Schema(description = "分类描述", maxLength = 255)
    private String description;

    /** 排序值。 */
    @Schema(description = "排序值")
    private Integer sort;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
