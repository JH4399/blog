package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Tags;
import org.apache.ibatis.annotations.Mapper;

/** 标签数据访问接口。 */
@Mapper
public interface TagMapper extends BaseMapper<Tags> {
}
