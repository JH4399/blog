package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文章传输对象，用于接收新增或修改参数。
 * 对应 {@link com.example.blog.entity.Posts}，字段独立声明。
 * 新增时不传 ID，修改时以路径 ID 为准；PUT 完整替换可编辑字段。
 * 作者和发布时间由服务端确定。
 */
@Getter
@Setter
@Schema(description = "文章传输对象")
public class PostsDTO {

    /** 文章 ID，新增时不传，修改时用于定位记录。 */
    @Schema(description = "文章 ID")
    @Positive(message = "文章 ID 必须为正整数")
    private Long id;

    /** 作者 ID。 */
    @Schema(description = "作者 ID，可省略；传入时必须与服务端确定的作者一致")
    @Positive(message = "作者 ID 必须为正整数")
    private Long userId;

    /** 分类 ID。 */
    @Schema(description = "分类 ID")
    @Positive(message = "分类 ID 必须为正整数")
    private Long categoryId;

    /** 标题，最长 200 个字符。 */
    @Schema(description = "标题", maxLength = 200)
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最多 200 个字符")
    private String title;

    /** URL 别名，最长 200 个字符。 */
    @Schema(description = "URL 别名", maxLength = 200)
    @NotBlank(message = "文章别名不能为空")
    @Size(max = 200, message = "文章别名最多 200 个字符")
    private String slug;

    /** 摘要，最长 500 个字符。 */
    @Schema(description = "摘要", maxLength = 500)
    @Size(max = 500, message = "摘要最多 500 个字符")
    private String summary;

    /** 正文（Markdown/HTML）。 */
    @Schema(description = "正文（Markdown/HTML）")
    @NotBlank(message = "正文不能为空")
    private String content;

    /** 封面图 URL，最长 255 个字符。 */
    @Schema(description = "封面图 URL", maxLength = 255)
    @Size(max = 255, message = "封面图 URL 最多 255 个字符")
    private String coverImage;

    /** 状态：0 草稿，1 已发布，2 私密。 */
    @Schema(description = "状态：0 草稿，1 已发布，2 私密")
    @Min(value = 0, message = "状态只能为 0、1 或 2")
    @Max(value = 2, message = "状态只能为 0、1 或 2")
    private Integer status;

    /** 是否置顶：0 否，1 是。 */
    @Schema(description = "是否置顶：0 否，1 是")
    @Min(value = 0, message = "是否置顶只能为 0 或 1")
    @Max(value = 1, message = "是否置顶只能为 0 或 1")
    private Integer isTop;

    /** 发布时间，由发布业务确定；未发布时可为空。 */
    @Schema(description = "发布时间，由服务端设置，请勿传入")
    @Null(message = "发布时间由服务端设置")
    private LocalDateTime publishedAt;
}
