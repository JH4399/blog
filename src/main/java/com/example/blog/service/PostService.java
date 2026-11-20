package com.example.blog.service;

import com.example.blog.dto.PostListQuery;
import com.example.blog.dto.PostsDTO;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.PostListVO;
import com.example.blog.vo.PostsVO;
import com.example.blog.vo.UsersVO;

import java.util.Map;

/** 文章管理及公开阅读业务接口。 */
public interface PostService {

    PostsVO create(PostsDTO request, UsersVO user);

    PostsVO update(Long id, PostsDTO request, UsersVO user);

    void delete(Long id, UsersVO user);

    PostsVO get(Long id, UsersVO user);

    /** 公开阅读，仅返回已发布文章；readerId 来自验证后的登录态，remoteAddress 来自服务端。 */
    PostsVO read(Long id, Long readerId, String remoteAddress, boolean countView);

    PageResult<PostListVO> list(PostListQuery query, UsersVO user);

    Map<String, Object> summary(UsersVO user);
}
