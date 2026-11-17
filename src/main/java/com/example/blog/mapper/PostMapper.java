package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.dto.PostListQuery;
import com.example.blog.entity.Posts;
import com.example.blog.vo.PostListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/** 文章数据访问接口。软删除过滤由业务层显式添加。 */
@Mapper
public interface PostMapper extends BaseMapper<Posts> {
    IPage<PostListVO> selectListPage(IPage<PostListVO> page, @Param("query") PostListQuery query,
            @Param("visibleAuthorId") Long visibleAuthorId);

    @Select("""
            SELECT COUNT(*) AS "posts",
                   COALESCE(SUM(CASE WHEN "status"=1 THEN 1 ELSE 0 END),0) AS "published",
                   COALESCE(SUM("view_count"),0) AS "views",
                   COALESCE(SUM("like_count"),0) AS "likes"
            FROM "BLOG"."posts" WHERE "deleted_at" IS NULL
            """)
    Map<String, Object> selectSummary();
}
