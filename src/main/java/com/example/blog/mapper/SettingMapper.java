package com.example.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blog.entity.Settings;
import org.apache.ibatis.annotations.Mapper;

/** 系统配置数据访问接口。 */
@Mapper
public interface SettingMapper extends BaseMapper<Settings> {
}
