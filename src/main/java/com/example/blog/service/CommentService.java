package com.example.blog.service;

import com.example.blog.dto.CommentsDTO;
import com.example.blog.vo.CommentsVO;
import com.example.blog.vo.UsersVO;

import java.util.List;

public interface CommentService {

    CommentsVO create(CommentsDTO request, UsersVO user, String ip, String userAgent);

    void delete(Long id, UsersVO user);

    List<CommentsVO> list(Long postId, UsersVO user);

    CommentsVO get(Long id, UsersVO user);
}
