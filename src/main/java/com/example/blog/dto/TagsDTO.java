package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Tags}，字段独立声明。
 * 新增时不传 ID，修改时使用 ID 定位记录；字段校验由具体接口负责。
 */
@Getter
@Setter
@Schema(description = "标签传输对象")
public class TagsDTO {

    /** 标签 ID，新增时不传，修改时用于定位记录。 */
    @Schema(description = "标签 ID")
    private Long id;

    /** 标签名称，最长 50 个字符。 */
    @Schema(description = "标签名称", maxLength = 50)
    private String name;

    /** URL 别名，最长 50 个字符。 */
    @Schema(description = "URL 别名", maxLength = 50)
    private String slug;
}
