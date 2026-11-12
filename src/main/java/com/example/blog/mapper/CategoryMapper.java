package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Categories;
import org.apache.ibatis.annotations.Mapper;

/** 分类数据访问接口。 */
@Mapper
public interface CategoryMapper extends BaseMapper<Categories> {
}
