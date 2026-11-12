package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.UserLoginDTO;
import com.example.blog.dto.UserRegisterDTO;
import com.example.blog.entity.Users;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.UserService;
import com.example.blog.service.TokenService;
import com.example.blog.vo.UserLoginVO;
import com.example.blog.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 用户注册、登录及登录态业务实现。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // 用户不存在时也执行 BCrypt 校验，减少由密码运算耗时产生的账户枚举差异。
    private static final String DUMMY_PASSWORD_HASH =
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    @Override
    @Transactional
    public UsersVO register(UserRegisterDTO request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw BusinessException.badRequest("密码 UTF-8 编码不能超过 72 字节");
        }
        if (userMapper.selectCount(Wrappers.<Users>lambdaQuery()
                .eq(Users::getUsername, request.getUsername())) > 0) {
            throw BusinessException.conflict("用户名已存在");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        user.setBio(request.getBio());
        user.setRole(1);
        user.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        try {
            // 数据库 username 唯一约束负责兜底并发注册，预查询不能代替唯一约束。
            userMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "注册信息已存在，请更换用户名或邮箱", exception);
        }

        return toVO(user);
    }

    @Override
    public UserLoginVO login(UserLoginDTO request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw BusinessException.badRequest("密码 UTF-8 编码不能超过 72 字节");
        }
        Users user = userMapper.selectOne(Wrappers.<Users>lambdaQuery()
                .eq(Users::getUsername, request.getUsername()));
        String hash = user == null ? DUMMY_PASSWORD_HASH : user.getPassword();
        boolean matches = passwordEncoder.matches(request.getPassword(), hash);
        if (user == null || !matches) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }
        requireActive(user);
        TokenService.IssuedToken issued = tokenService.issue(user.getId());
        return new UserLoginVO(issued.value(), "Bearer", issued.expiresIn(), toVO(user));
    }

    @Override
    public UsersVO currentUser(String token) {
        Long userId = tokenService.requireUserId(token);
        Users user = userMapper.selectById(userId);
        if (user == null) {
            tokenService.revoke(token);
            throw BusinessException.unauthorized("用户不存在，请重新登录");
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            tokenService.revoke(token);
            throw BusinessException.forbidden("账号已被禁用");
        }
        return toVO(user);
    }

    @Override
    public void logout(String token) {
        tokenService.revoke(token);
    }

    private static void requireActive(Users user) {
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw BusinessException.forbidden("账号已被禁用");
        }
    }

    private static UsersVO toVO(Users user) {
        UsersVO result = new UsersVO();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setNickname(user.getNickname());
        result.setEmail(user.getEmail());
        result.setAvatar(user.getAvatar());
        result.setBio(user.getBio());
        result.setRole(user.getRole());
        result.setStatus(user.getStatus());
        result.setCreatedAt(user.getCreatedAt());
        result.setUpdatedAt(user.getUpdatedAt());
        return result;
    }
}
