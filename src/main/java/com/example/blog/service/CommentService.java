package com.example.blog.service;

import com.example.blog.dto.CommentsDTO;
import com.example.blog.vo.CommentsVO;
import com.example.blog.vo.UsersVO;

import java.util.List;

public interface CommentService {

    CommentsVO create(CommentsDTO request, UsersVO user, String ip, String userAgent);

    void delete(Long id, UsersVO user);

    /** user 可空，游客仅可查看已发布文章的评论。 */
    List<CommentsVO> list(Long postId, UsersVO user);

    /** user 可空，可见范围与列表一致。 */
    CommentsVO get(Long id, UsersVO user);
}
