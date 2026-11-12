package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文章传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Posts}，字段独立声明。
 * 新增时不传 ID，修改时使用 ID 定位记录；字段校验由具体接口负责。
 * userId 应由服务端根据登录状态或后台授权确定。
 */
@Getter
@Setter
@Schema(description = "文章传输对象")
public class PostsDTO {

    /** 文章 ID，新增时不传，修改时用于定位记录。 */
    @Schema(description = "文章 ID")
    private Long id;

    /** 作者 ID。 */
    @Schema(description = "作者 ID")
    private Long userId;

    /** 分类 ID。 */
    @Schema(description = "分类 ID")
    private Long categoryId;

    /** 标题，最长 200 个字符。 */
    @Schema(description = "标题", maxLength = 200)
    private String title;

    /** URL 别名，最长 200 个字符。 */
    @Schema(description = "URL 别名", maxLength = 200)
    private String slug;

    /** 摘要，最长 500 个字符。 */
    @Schema(description = "摘要", maxLength = 500)
    private String summary;

    /** 正文（Markdown/HTML）。 */
    @Schema(description = "正文（Markdown/HTML）")
    private String content;

    /** 封面图 URL，最长 255 个字符。 */
    @Schema(description = "封面图 URL", maxLength = 255)
    private String coverImage;

    /** 状态：0 草稿，1 已发布，2 私密。 */
    @Schema(description = "状态：0 草稿，1 已发布，2 私密")
    private Integer status;

    /** 是否置顶：0 否，1 是。 */
    @Schema(description = "是否置顶：0 否，1 是")
    private Integer isTop;

    /** 发布时间，由发布业务确定；未发布时可为空。 */
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
}
