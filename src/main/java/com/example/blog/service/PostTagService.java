package com.example.blog.service;

import com.example.blog.dto.PostTagsDTO;
import com.example.blog.vo.PostTagsVO;
import com.example.blog.vo.UsersVO;

import java.util.List;

/** 文章标签关联业务接口。 */
public interface PostTagService {

    PostTagsVO create(PostTagsDTO request, UsersVO user);

    PostTagsVO update(Long postId, Long tagId, PostTagsDTO request, UsersVO user);

    void delete(Long postId, Long tagId, UsersVO user);

    List<PostTagsVO> list(Long postId, UsersVO user);

    PostTagsVO get(Long postId, Long tagId, UsersVO user);
}
