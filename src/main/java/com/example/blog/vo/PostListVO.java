package com.example.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/** 文章分页条目，保留原有分页字段，不装载正文。 */
@Getter
@Setter
@Schema(description = "文章分页条目")
public class PostListVO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String slug;
    @Schema(description = "文章封面图片地址，可为空")
    private String coverImage;
    @Schema(description = "状态：0草稿、1已发布、2私密")
    private Integer status;
    private Integer isTop;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private String authorName;
    private String categoryName;
}
