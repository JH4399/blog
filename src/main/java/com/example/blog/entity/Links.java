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
 * 友情链接实体，对应 BLOG.links 表。
 */
@Getter
@Setter
@Schema(description = "友情链接")
@TableName(value = "\"links\"", schema = "\"BLOG\"")
public class Links {

    /** 友情链接 ID，数据库自增。 */
    @Schema(description = "友情链接 ID")
    @TableId(value = "\"id\"", type = IdType.AUTO)
    private Long id;

    /** 站点名称，对应 name，必填，最长 50 个字符。 */
    @Schema(description = "站点名称", maxLength = 50)
    @TableField("\"name\"")
    private String name;

    /** 站点 URL，对应 url，必填，最长 255 个字符。 */
    @Schema(description = "站点 URL", maxLength = 255)
    @TableField("\"url\"")
    private String url;

    /** 站点 Logo，对应 logo，可空，最长 255 个字符。 */
    @Schema(description = "站点 Logo", maxLength = 255)
    @TableField(value = "\"logo\"", updateStrategy = FieldStrategy.ALWAYS)
    private String logo;

    /** 站点描述，对应 description，可空，最长 255 个字符。 */
    @Schema(description = "站点描述", maxLength = 255)
    @TableField(value = "\"description\"", updateStrategy = FieldStrategy.ALWAYS)
    private String description;

    /** 排序值，对应 sort，非空，数据库默认值为 0。 */
    @Schema(description = "排序值")
    @TableField("\"sort\"")
    private Integer sort;

    /** 状态，对应 status，必填：0 隐藏，1 显示；数据库默认值为 1。 */
    @Schema(description = "状态：0 隐藏，1 显示")
    @TableField("\"status\"")
    private Integer status;

    /** 创建时间，对应 created_at，非空，数据库默认值为当前时间。 */
    @Schema(description = "创建时间")
    @TableField(value = "\"created_at\"", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdAt;
}
