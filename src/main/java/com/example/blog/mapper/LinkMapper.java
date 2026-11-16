package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Links;
import org.apache.ibatis.annotations.Mapper;

/** 友情链接数据访问接口。 */
@Mapper
public interface LinkMapper extends BaseMapper<Links> {
}
