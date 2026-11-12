package com.example.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "\"categories\"", schema = "\"BLOG\"")
public class Categories {

    /** 分类 ID，数据库自增。 */
    @Schema(description = "分类 ID")
    @TableId(value = "\"id\"", type = IdType.AUTO)
    private Long id;

    /** 分类名称，对应 name，必填，最长 50 个字符。 */
    @Schema(description = "分类名称", maxLength = 50)
    @TableField("\"name\"")
    private String name;

    /** URL 别名，对应 slug，必填，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    @TableField("\"slug\"")
    private String slug;

    /** 分类描述，对应 description，可空，最长 255 个字符。 */
    @Schema(description = "分类描述", maxLength = 255)
    @TableField(value = "\"description\"", updateStrategy = FieldStrategy.ALWAYS)
    private String description;

    /** 排序值，对应 sort，非空，数据库默认值为 0。 */
    @Schema(description = "排序值")
    @TableField("\"sort\"")
    private Integer sort;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    @TableField("\"created_at\"")
    private LocalDateTime createdAt;

    /** 更新时间，对应 updated_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "更新时间")
    @TableField("\"updated_at\"")
    private LocalDateTime updatedAt;
}
