package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Posts;
import org.apache.ibatis.annotations.Mapper;

/** 文章数据访问接口。软删除过滤由业务层显式添加。 */
@Mapper
public interface PostMapper extends BaseMapper<Posts> {
}
