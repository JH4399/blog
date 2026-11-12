package com.example.blog.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 分类实体，对应 BLOG.categories 表。
 */
@Getter
@Setter
@Schema(description = "分类")
public class Categories {

    /** 分类 ID，数据库自增。 */
    @Schema(description = "分类 ID")
    private Long id;

    /** 分类名称，对应 name，必填，最长 50 个字符。 */
    @Schema(description = "分类名称", maxLength = 50)
    private String name;

    /** URL 别名，对应 slug，必填，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    private String slug;

    /** 分类描述，对应 description，可空，最长 255 个字符。 */
    @Schema(description = "分类描述", maxLength = 255)
    private String description;

    /** 排序值，对应 sort，非空，数据库默认值为 0。 */
    @Schema(description = "排序值")
    private Integer sort;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间，对应 updated_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
