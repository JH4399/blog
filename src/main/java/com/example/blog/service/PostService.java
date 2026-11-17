package com.example.blog.service;

import com.example.blog.dto.PostListQuery;
import com.example.blog.dto.PostsDTO;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.PostListVO;
import com.example.blog.vo.PostsVO;
import com.example.blog.vo.UsersVO;

import java.util.Map;

/** 文章管理接口，当前用户来自登录拦截器。 */
public interface PostService {

    PostsVO create(PostsDTO request, UsersVO user);

    PostsVO update(Long id, PostsDTO request, UsersVO user);

    void delete(Long id, UsersVO user);

    PostsVO get(Long id, UsersVO user);

    PageResult<PostListVO> list(PostListQuery query, UsersVO user);

    Map<String, Object> summary(UsersVO user);
}
