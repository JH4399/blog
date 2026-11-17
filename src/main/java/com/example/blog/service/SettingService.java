package com.example.blog.service;

import com.example.blog.dto.SearchListQuery;
import com.example.blog.dto.SettingsDTO;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.SettingsVO;
import com.example.blog.vo.UsersVO;


/** 系统配置业务接口，仅管理员可访问。 */
public interface SettingService {

    SettingsVO create(SettingsDTO request, UsersVO user);

    SettingsVO update(Long id, SettingsDTO request, UsersVO user);

    void delete(Long id, UsersVO user);

    SettingsVO get(Long id, UsersVO user);

    PageResult<SettingsVO> list(SearchListQuery query, UsersVO user);
}
