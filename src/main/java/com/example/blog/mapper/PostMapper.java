package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.dto.PostListQuery;
import com.example.blog.entity.Posts;
import com.example.blog.vo.PostListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/** 文章数据访问接口。软删除过滤由业务层显式添加。 */
@Mapper
public interface PostMapper extends BaseMapper<Posts> {
    /** 仅累加仍公开的文章，不改变文章更新时间或其他统计。 */
    @Update("""
            UPDATE "BLOG"."posts"
            SET "view_count" = "view_count" + 1
            WHERE "id" = #{id} AND "status" = 1 AND "deleted_at" IS NULL
            """)
    int incrementPublishedViewCount(@Param("id") Long id);

    IPage<PostListVO> selectListPage(IPage<PostListVO> page, @Param("query") PostListQuery query,
            @Param("viewerId") Long viewerId);

    @Select("""
            SELECT COUNT(*) AS "posts",
                   COALESCE(SUM(CASE WHEN "status"=1 THEN 1 ELSE 0 END),0) AS "published",
                   COALESCE(SUM("view_count"),0) AS "views",
                   COALESCE(SUM("like_count"),0) AS "likes"
            FROM "BLOG"."posts" WHERE "deleted_at" IS NULL
            """)
    Map<String, Object> selectSummary();
}
