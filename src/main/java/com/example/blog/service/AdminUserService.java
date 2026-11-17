package com.example.blog.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.AdminUserUpdateDTO;
import com.example.blog.entity.Users;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserMapper userMapper;

    public List<UsersVO> list(UsersVO currentUser) {
        requireAdmin(currentUser);
        return userMapper.selectList(Wrappers.<Users>lambdaQuery()
                        .orderByDesc(Users::getCreatedAt, Users::getId))
                .stream().map(AdminUserService::toVO).toList();
    }

    @Transactional
    public UsersVO update(Long id, AdminUserUpdateDTO request, UsersVO currentUser) {
        requireAdmin(currentUser);
        if (id.equals(currentUser.getId()) &&
                (!Integer.valueOf(2).equals(request.getRole()) || !Integer.valueOf(1).equals(request.getStatus()))) {
            throw BusinessException.conflict("不能禁用当前登录账户或降低自己的管理员角色");
        }
        if (userMapper.selectById(id) == null) {
            throw BusinessException.notFound("用户不存在");
        }
        try {
            int changed = userMapper.update(null, Wrappers.<Users>lambdaUpdate().eq(Users::getId, id)
                    .set(Users::getNickname, request.getNickname())
                    .set(Users::getEmail, request.getEmail())
                    .set(Users::getAvatar, request.getAvatar())
                    .set(Users::getBio, request.getBio())
                    .set(Users::getRole, request.getRole())
                    .set(Users::getStatus, request.getStatus())
                    .set(Users::getUpdatedAt, LocalDateTime.now()));
            if (changed == 0) {
                throw BusinessException.notFound("用户不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户资料与现有记录冲突，请检查邮箱等字段", exception);
        }
        return toVO(userMapper.selectById(id));
    }

    private static void requireAdmin(UsersVO currentUser) {
        if (currentUser == null || !Integer.valueOf(2).equals(currentUser.getRole()) ||
                !Integer.valueOf(1).equals(currentUser.getStatus())) {
            throw BusinessException.forbidden("仅管理员可管理用户");
        }
    }

    private static UsersVO toVO(Users user) {
        UsersVO result = new UsersVO();
        BeanUtils.copyProperties(user, result);
        return result;
    }
}
