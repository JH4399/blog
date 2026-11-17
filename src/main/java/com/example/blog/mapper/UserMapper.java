package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.blog.dto.UserListQuery;
import com.example.blog.entity.Users;
import com.example.blog.vo.UserListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 用户数据访问接口，由 MyBatis-Plus 提供查询、新增等 SQL。
 */
@Mapper
public interface UserMapper extends BaseMapper<Users> {
    IPage<UserListVO> selectListPage(IPage<UserListVO> page, @Param("query") UserListQuery query);

    @Select("""
            SELECT COUNT(*) AS "users",
                   COALESCE(SUM(CASE WHEN "status"=1 THEN 1 ELSE 0 END),0) AS "activeUsers"
            FROM "BLOG"."users"
            """)
    Map<String, Object> selectSummary();
}
