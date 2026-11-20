package com.example.blog.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.CommentsDTO;
import com.example.blog.entity.Comments;
import com.example.blog.entity.Posts;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.service.CommentService;
import com.example.blog.vo.CommentsVO;
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
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public CommentsVO create(CommentsDTO request, UsersVO user, String ip, String userAgent) {
        if (request.getUserId() != null && !user.getId().equals(request.getUserId())) {
            throw BusinessException.badRequest("评论用户由登录状态确定，不能指定其他用户");
        }
        Posts post = requirePost(request.getPostId(), user, true);
        if (request.getParentId() != null) {
            Comments parent = requireComment(request.getParentId());
            if (!post.getId().equals(parent.getPostId())) {
                throw BusinessException.badRequest("父评论必须属于同一篇文章");
            }
        }
        Comments comment = new Comments();
        comment.setPostId(post.getId());
        comment.setUserId(user.getId());
        comment.setParentId(request.getParentId());
        String nickname = request.getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = user.getNickname();
            if (nickname == null || nickname.isBlank()) {
                nickname = user.getUsername();
            }
        }
        comment.setNickname(nickname);
        comment.setEmail(request.getEmail());
        comment.setWebsite(request.getWebsite());
        comment.setContent(request.getContent());
        // 只记录连接地址，不信任客户端可伪造的转发头。
        comment.setIp(truncate(ip, 45));
        comment.setUserAgent(truncate(userAgent, 255));
        comment.setCreatedAt(LocalDateTime.now());
        try {
            commentMapper.insert(comment);
            postMapper.update(null, Wrappers.<Posts>lambdaUpdate().eq(Posts::getId, post.getId())
                    .setSql("\"comment_count\" = \"comment_count\" + 1"));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "文章、用户或父评论数据已变更，请刷新后重试", exception);
        }
        return toVO(comment);
    }

    @Override
    @Transactional
    public void delete(Long id, UsersVO user) {
        Comments comment = requireComment(id);
        Posts post = requirePost(comment.getPostId(), user, true);
        // 同一文章的新增和删除以文章行锁串行化，防止删除父评论时并发插入回复。
        comment = requireComment(id);
        if (!canManage(post, user) && !user.getId().equals(comment.getUserId())) {
            throw BusinessException.forbidden("无权删除该评论");
        }
        if (commentMapper.selectCount(Wrappers.<Comments>lambdaQuery().eq(Comments::getParentId, id)) > 0) {
            throw BusinessException.conflict("评论存在回复，请先删除回复");
        }
        try {
            if (commentMapper.deleteById(id) == 0) {
                throw BusinessException.notFound("评论不存在");
            }
            postMapper.update(null, Wrappers.<Posts>lambdaUpdate().eq(Posts::getId, post.getId())
                    .setSql("\"comment_count\" = CASE WHEN \"comment_count\" > 0 THEN \"comment_count\" - 1 ELSE 0 END"));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "评论关联数据已变更，请刷新后重试", exception);
        }
    }

    @Override
    public List<CommentsVO> list(Long postId, UsersVO user) {
        requirePost(postId, user, false);
        return commentMapper.selectList(Wrappers.<Comments>lambdaQuery()
                        .eq(Comments::getPostId, postId)
                        .orderByAsc(Comments::getCreatedAt, Comments::getId))
                .stream().map(CommentServiceImpl::toVO).toList();
    }

    @Override
    public CommentsVO get(Long id, UsersVO user) {
        Comments comment = requireComment(id);
        requirePost(comment.getPostId(), user, false);
        return toVO(comment);
    }

    private Posts requirePost(Long id, UsersVO user, boolean lock) {
        var query = Wrappers.<Posts>lambdaQuery().eq(Posts::getId, id).isNull(Posts::getDeletedAt);
        if (lock) {
            query.last("FOR UPDATE");
        }
        Posts post = postMapper.selectOne(query);
        if (post == null) {
            throw BusinessException.notFound("文章不存在或已删除");
        }
        if (!Integer.valueOf(1).equals(post.getStatus()) && !canManage(post, user)) {
            throw BusinessException.forbidden("无权访问该文章的评论");
        }
        return post;
    }

    private Comments requireComment(Long id) {
        Comments comment = commentMapper.selectById(id);
        if (comment == null) {
            throw BusinessException.notFound("评论不存在");
        }
        return comment;
    }

    private static boolean canManage(Posts post, UsersVO user) {
        return Integer.valueOf(2).equals(user.getRole()) || user.getId().equals(post.getUserId());
    }

    private static String truncate(String value, int maxLength) {
        return value == null || value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private static CommentsVO toVO(Comments comment) {
        CommentsVO result = new CommentsVO();
        BeanUtils.copyProperties(comment, result);
        return result;
    }
}
