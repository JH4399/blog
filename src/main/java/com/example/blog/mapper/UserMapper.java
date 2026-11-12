package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口，由 MyBatis-Plus 提供查询、新增等 SQL。
 */
@Mapper
public interface UserMapper extends BaseMapper<Users> {
}
