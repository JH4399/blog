package com.example.blog.mapper;

import com.example.blog.entity.PostTags;
import org.apache.ibatis.annotations.*;

import java.util.List;

/** 以 post_id、tag_id 共同定位关联，不使用单主键的 ById 方法。 */
@Mapper
public interface PostTagMapper {

    @Insert("""
            INSERT INTO "BLOG"."post_tags" ("post_id", "tag_id") VALUES (#{postId}, #{tagId})
            """)
    int insert(PostTags relation);

    @Update("""
            UPDATE "BLOG"."post_tags" SET "tag_id" = #{newTagId}
            WHERE "post_id" = #{postId} AND "tag_id" = #{tagId}
            """)
    int updateTag(@Param("postId") Long postId, @Param("tagId") Long tagId, @Param("newTagId") Long newTagId);

    @Delete("""
            DELETE FROM "BLOG"."post_tags" WHERE "post_id" = #{postId} AND "tag_id" = #{tagId}
            """)
    int delete(@Param("postId") Long postId, @Param("tagId") Long tagId);

    @Select("""
            SELECT "post_id", "tag_id" FROM "BLOG"."post_tags"
            WHERE "post_id" = #{postId} AND "tag_id" = #{tagId}
            """)
    @Results(id = "postTag", value = {
            @Result(column = "post_id", property = "postId", id = true),
            @Result(column = "tag_id", property = "tagId", id = true)
    })
    PostTags select(@Param("postId") Long postId, @Param("tagId") Long tagId);

    @Select("""
            SELECT "post_id", "tag_id" FROM "BLOG"."post_tags" WHERE "post_id" = #{postId} ORDER BY "tag_id"
            """)
    @ResultMap("postTag")
    List<PostTags> selectByPostId(@Param("postId") Long postId);
}
