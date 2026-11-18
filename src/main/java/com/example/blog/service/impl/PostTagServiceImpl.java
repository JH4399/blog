package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.PostTagsDTO;
import com.example.blog.entity.PostTags;
import com.example.blog.entity.Posts;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.PostTagMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.service.PostTagService;
import com.example.blog.vo.PostTagsVO;
import com.example.blog.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostTagServiceImpl implements PostTagService {

    private final PostTagMapper postTagMapper;
    private final PostMapper postMapper;
    private final TagMapper tagMapper;

    @Override
    @Transactional
    public PostTagsVO create(PostTagsDTO request, UsersVO user) {
        requireManageablePost(request.getPostId(), user);
        requireTag(request.getTagId());
        requireUniqueRelation(request.getPostId(), request.getTagId());
        PostTags relation = new PostTags();
        relation.setPostId(request.getPostId());
        relation.setTagId(request.getTagId());
        try {
            postTagMapper.insert(relation);
        } catch (DuplicateKeyException exception) {
            throw duplicateRelation(exception);
        } catch (DataIntegrityViolationException exception) {
            throw changedReferences(exception);
        }
        return toVO(relation);
    }

    @Override
    @Transactional
    public PostTagsVO update(Long postId, Long tagId, PostTagsDTO request, UsersVO user) {
        requireManageablePost(postId, user);
        if (!postId.equals(request.getPostId())) {
            throw BusinessException.badRequest("请求体文章 ID 必须与路径文章 ID 一致");
        }
        PostTags relation = requireRelation(postId, tagId);
        if (tagId.equals(request.getTagId())) {
            return toVO(relation);
        }
        requireTag(request.getTagId());
        requireUniqueRelation(postId, request.getTagId());
        try {
            if (postTagMapper.updateTag(postId, tagId, request.getTagId()) == 0) {
                throw BusinessException.notFound("文章标签关联不存在");
            }
        } catch (DuplicateKeyException exception) {
            throw duplicateRelation(exception);
        } catch (DataIntegrityViolationException exception) {
            throw changedReferences(exception);
        }
        relation.setTagId(request.getTagId());
        return toVO(relation);
    }

    @Override
    @Transactional
    public void delete(Long postId, Long tagId, UsersVO user) {
        requireManageablePost(postId, user);
        try {
            if (postTagMapper.delete(postId, tagId) == 0) {
                throw BusinessException.notFound("文章标签关联不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "文章标签关联存在关联数据，无法删除", exception);
        }
    }

    @Override
    public List<PostTagsVO> list(Long postId, UsersVO user) {
        requireReadablePost(postId, user);
        return postTagMapper.selectByPostId(postId).stream().map(PostTagServiceImpl::toVO).toList();
    }

    @Override
    public PostTagsVO get(Long postId, Long tagId, UsersVO user) {
        requireReadablePost(postId, user);
        return toVO(requireRelation(postId, tagId));
    }

    private Posts requirePost(Long postId, boolean lock) {
        var query = Wrappers.<Posts>lambdaQuery().eq(Posts::getId, postId).isNull(Posts::getDeletedAt);
        if (lock) {
            // 与文章更新、软删除共享文章行锁，串行化同一文章的关联写入。
            query.last("FOR UPDATE");
        }
        Posts post = postMapper.selectOne(query);
        if (post == null) {
            throw BusinessException.notFound("文章不存在或已删除");
        }
        return post;
    }

    private void requireReadablePost(Long postId, UsersVO user) {
        Posts post = requirePost(postId, false);
        if (!canManage(post, user) && !Integer.valueOf(1).equals(post.getStatus())) {
            throw BusinessException.forbidden("无权查看其他用户草稿或私密文章的标签关联");
        }
    }

    private void requireManageablePost(Long postId, UsersVO user) {
        Posts post = requirePost(postId, true);
        if (!canManage(post, user)) {
            throw BusinessException.forbidden("无权管理其他用户的文章标签关联");
        }
    }

    private static boolean canManage(Posts post, UsersVO user) {
        return Integer.valueOf(2).equals(user.getRole()) || user.getId().equals(post.getUserId());
    }

    private void requireTag(Long tagId) {
        if (tagMapper.selectById(tagId) == null) {
            throw BusinessException.notFound("标签不存在");
        }
    }

    private PostTags requireRelation(Long postId, Long tagId) {
        PostTags relation = postTagMapper.select(postId, tagId);
        if (relation == null) {
            throw BusinessException.notFound("文章标签关联不存在");
        }
        return relation;
    }

    private void requireUniqueRelation(Long postId, Long tagId) {
        if (postTagMapper.select(postId, tagId) != null) {
            throw BusinessException.conflict("文章已绑定该标签");
        }
    }

    private static BusinessException duplicateRelation(DuplicateKeyException exception) {
        return new BusinessException(HttpStatus.CONFLICT, "文章已绑定该标签", exception);
    }

    private static BusinessException changedReferences(DataIntegrityViolationException exception) {
        return new BusinessException(HttpStatus.CONFLICT, "文章或标签数据已变更，请刷新后重试", exception);
    }

    private static PostTagsVO toVO(PostTags relation) {
        PostTagsVO result = new PostTagsVO();
        result.setPostId(relation.getPostId());
        result.setTagId(relation.getTagId());
        return result;
    }
}
