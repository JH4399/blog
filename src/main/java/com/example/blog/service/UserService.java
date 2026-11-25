package com.example.blog.service;

import com.example.blog.dto.UsersDTO;
import com.example.blog.dto.UserStatusDTO;
import com.example.blog.dto.UserListQuery;
import com.example.blog.dto.UserLoginDTO;
import com.example.blog.dto.UserRegisterDTO;
import com.example.blog.vo.UserLoginVO;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.UserListVO;
import com.example.blog.vo.UsersVO;

/**
 * 用户业务接口。
 */
public interface UserService {

    /** 管理员分页查询用户，不返回密码。 */
    PageResult<UserListVO> list(UserListQuery query, UsersVO currentUser);

    /** 登录用户仅可修改本人资料，不修改角色、状态和登录凭据。 */
    UsersVO update(Long id, UsersDTO request, UsersVO currentUser);

    /** 管理员仅可修改用户状态，禁止禁用当前登录账户。 */
    UsersVO updateStatus(Long id, UserStatusDTO request, UsersVO currentUser);

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
