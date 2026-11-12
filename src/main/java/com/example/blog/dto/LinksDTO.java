package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 友情链接传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Links}，字段独立声明。
 * 新增时不传 ID，修改时使用 ID 定位记录；字段校验由具体接口负责。
 */
@Getter
@Setter
@Schema(description = "友情链接传输对象")
public class LinksDTO {

    /** 友情链接 ID，新增时不传，修改时用于定位记录。 */
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
}
