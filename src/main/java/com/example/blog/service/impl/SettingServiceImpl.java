package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.dto.ListQuery;
import com.example.blog.dto.SettingsDTO;
import com.example.blog.entity.Settings;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.SettingMapper;
import com.example.blog.service.SettingService;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.SettingsVO;
import com.example.blog.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final SettingMapper settingMapper;

    @Override
    @Transactional
    public SettingsVO create(SettingsDTO request, UsersVO user) {
        requireAdmin(user);
        if (request.getId() != null) {
            throw BusinessException.badRequest("新增配置时不能指定 ID");
        }
        requireUniqueKey(request.getConfigKey(), null);
        Settings setting = new Settings();
        applyFields(setting, request);
        setting.setUpdatedAt(LocalDateTime.now());
        try {
            settingMapper.insert(setting);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "配置键已存在", exception);
        }
        return toVO(setting);
    }

    @Override
    @Transactional
    public SettingsVO update(Long id, SettingsDTO request, UsersVO user) {
        requireAdmin(user);
        if (request.getId() != null && !id.equals(request.getId())) {
            throw BusinessException.badRequest("请求体 ID 必须与路径 ID 一致");
        }
        Settings setting = requireSetting(id);
        requireUniqueKey(request.getConfigKey(), id);
        applyFields(setting, request);
        setting.setUpdatedAt(LocalDateTime.now());
        try {
            if (settingMapper.updateById(setting) == 0) {
                throw BusinessException.notFound("配置不存在");
            }
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "配置键已存在", exception);
        }
        return toVO(setting);
    }

    @Override
    @Transactional
    public void delete(Long id, UsersVO user) {
        requireAdmin(user);
        try {
            if (settingMapper.deleteById(id) == 0) {
                throw BusinessException.notFound("配置不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "配置存在关联数据，无法删除", exception);
        }
    }

    @Override
    public PageResult<SettingsVO> list(ListQuery query, UsersVO user) {
        requireAdmin(user);
        String keyword = query.getQ() == null ? "" : query.getQ().trim();
        var wrapper = Wrappers.<Settings>lambdaQuery()
                .and(!keyword.isEmpty(), w -> w.like(Settings::getConfigKey, keyword)
                        .or().like(Settings::getRemark, keyword))
                .orderByAsc(Settings::getId);
        var page = settingMapper.selectPage(new Page<Settings>(query.getPage(), query.getSize()), wrapper);
        var records = page.getRecords().stream().map(SettingServiceImpl::toVO).toList();
        return new PageResult<>(records, page.getTotal(), query.getPage(), query.getSize(), page.getPages());
    }

    @Override
    public SettingsVO get(Long id, UsersVO user) {
        requireAdmin(user);
        return toVO(requireSetting(id));
    }

    private Settings requireSetting(Long id) {
        Settings setting = settingMapper.selectById(id);
        if (setting == null) {
            throw BusinessException.notFound("配置不存在");
        }
        return setting;
    }

    private void requireUniqueKey(String key, Long excludedId) {
        if (settingMapper.selectCount(Wrappers.<Settings>lambdaQuery()
                .eq(Settings::getConfigKey, key)
                .ne(excludedId != null, Settings::getId, excludedId)) > 0) {
            throw BusinessException.conflict("配置键已存在");
        }
    }

    private static void requireAdmin(UsersVO user) {
        if (user == null || !Integer.valueOf(2).equals(user.getRole())) {
            throw BusinessException.forbidden("仅管理员可访问系统配置");
        }
    }

    private static void applyFields(Settings setting, SettingsDTO request) {
        setting.setConfigKey(request.getConfigKey());
        setting.setConfigValue(request.getConfigValue());
        setting.setRemark(request.getRemark());
    }

    private static SettingsVO toVO(Settings setting) {
        SettingsVO result = new SettingsVO();
        BeanUtils.copyProperties(setting, result);
        return result;
    }
}
