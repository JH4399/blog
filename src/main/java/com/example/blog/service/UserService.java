package com.example.blog.service;

import com.example.blog.dto.UserLoginDTO;
import com.example.blog.dto.UserRegisterDTO;
import com.example.blog.vo.UserLoginVO;
import com.example.blog.vo.UsersVO;

/**
 * 用户业务接口。
 */
public interface UserService {

    /**
     * 校验用户名是否重复，加密密码并注册普通用户。
     *
     * @param request 注册参数
     * @return 不含密码的用户资料
     */
    UsersVO register(UserRegisterDTO request);

    /** 校验凭据及账户状态，创建有固定有效期的 Redis 登录态。 */
    UserLoginVO login(UserLoginDTO request);

    /** 验证登录态并读取数据库中的最新用户资料、状态和角色。 */
    UsersVO currentUser(String token);

    /** 撤销当前 Token，不影响该用户在其他设备上的登录态。 */
    void logout(String token);
}
