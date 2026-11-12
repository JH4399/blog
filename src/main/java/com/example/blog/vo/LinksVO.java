package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 友情链接视图对象，用于返回展示数据。
 * 对应 {@link com.example.blog.entity.Links}，字段独立声明。
 */
@Getter
@Setter
@Schema(description = "友情链接视图对象")
public class LinksVO {

    /** 友情链接 ID，由数据库生成。 */
    @Schema(description = "友情链接 ID")
    private Long id;

    /** 站点名称，最长 50 个字符。 */
    @Schema(description = "站点名称", maxLength = 50)
    private String name;

    /** 站点 URL，最长 255 个字符。 */
    @Schema(description = "站点 URL", maxLength = 255)
    private String url;

    /** 站点 Logo，最长 255 个字符。 */
    @Schema(description = "站点 Logo", maxLength = 255)
    private String logo;

    /** 站点描述，最长 255 个字符。 */
    @Schema(description = "站点描述", maxLength = 255)
    private String description;

    /** 排序值。 */
    @Schema(description = "排序值")
    private Integer sort;

    /** 状态：0 隐藏，1 显示。 */
    @Schema(description = "状态：0 隐藏，1 显示")
    private Integer status;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
