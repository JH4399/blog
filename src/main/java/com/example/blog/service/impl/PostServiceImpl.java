package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.PostsDTO;
import com.example.blog.entity.Posts;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.PostMapper;
import com.example.blog.service.PostService;
import com.example.blog.vo.PostsVO;
import com.example.blog.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public PostsVO create(PostsDTO request, UsersVO user) {
        if (request.getId() != null) {
            throw BusinessException.badRequest("新增文章时不能指定 ID");
        }
        requireAuthor(request, user.getId());
        requireCategory(request.getCategoryId());
        requireUniqueSlug(request.getSlug(), null);
        Posts post = new Posts();
        post.setUserId(user.getId());
        LocalDateTime now = LocalDateTime.now();
        applyFields(post, request, now);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCreatedAt(now);
        try {
            postMapper.insert(post);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "文章别名已存在", exception);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "作者或分类数据已变更，请刷新后重试", exception);
        }
        return toVO(post);
    }

    @Override
    @Transactional
    public PostsVO update(Long id, PostsDTO request, UsersVO user) {
        if (request.getId() != null && !id.equals(request.getId())) {
            throw BusinessException.badRequest("请求体 ID 必须与路径 ID 一致");
        }
        Posts post = requireAccessiblePost(id, user);
        requireAuthor(request, post.getUserId());
        requireCategory(request.getCategoryId());
        requireUniqueSlug(request.getSlug(), id);
        applyFields(post, request, LocalDateTime.now());
        try {
            // 显式写入 null 以支持清空字段；只更新编辑字段，避免覆盖并发变化的统计值。
            int changed = postMapper.update(null, Wrappers.<Posts>lambdaUpdate()
                    .eq(Posts::getId, id).isNull(Posts::getDeletedAt)
                    .set(Posts::getCategoryId, post.getCategoryId())
                    .set(Posts::getTitle, post.getTitle())
                    .set(Posts::getSlug, post.getSlug())
                    .set(Posts::getSummary, post.getSummary())
                    .set(Posts::getContent, post.getContent())
                    .set(Posts::getCoverImage, post.getCoverImage())
                    .set(Posts::getStatus, post.getStatus())
                    .set(Posts::getIsTop, post.getIsTop())
                    .set(Posts::getPublishedAt, post.getPublishedAt())
                    .set(Posts::getUpdatedAt, post.getUpdatedAt()));
            if (changed == 0) {
                throw BusinessException.notFound("文章不存在或已删除");
            }
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "文章别名已存在", exception);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "作者或分类数据已变更，请刷新后重试", exception);
        }
        return toVO(post);
    }

    @Override
    @Transactional
    public void delete(Long id, UsersVO user) {
        requireAccessiblePost(id, user);
        LocalDateTime now = LocalDateTime.now();
        int changed = postMapper.update(null, Wrappers.<Posts>lambdaUpdate()
                .eq(Posts::getId, id).isNull(Posts::getDeletedAt)
                .set(Posts::getDeletedAt, now).set(Posts::getUpdatedAt, now));
        if (changed == 0) {
            throw BusinessException.notFound("文章不存在或已删除");
        }
    }

    @Override
    public List<PostsVO> list(UsersVO user) {
        return postMapper.selectList(Wrappers.<Posts>lambdaQuery()
                        .isNull(Posts::getDeletedAt)
                        .eq(!isAdmin(user), Posts::getUserId, user.getId())
                        .orderByDesc(Posts::getIsTop, Posts::getCreatedAt, Posts::getId))
                .stream().map(PostServiceImpl::toVO).toList();
    }

    @Override
    public PostsVO get(Long id, UsersVO user) {
        return toVO(requireAccessiblePost(id, user));
    }

    private Posts requireAccessiblePost(Long id, UsersVO user) {
        Posts post = postMapper.selectOne(Wrappers.<Posts>lambdaQuery()
                .eq(Posts::getId, id).isNull(Posts::getDeletedAt));
        if (post == null) {
            throw BusinessException.notFound("文章不存在或已删除");
        }
        if (!isAdmin(user) && !post.getUserId().equals(user.getId())) {
            throw BusinessException.forbidden("无权访问其他用户的文章");
        }
        return post;
    }

    private static boolean isAdmin(UsersVO user) {
        return Integer.valueOf(2).equals(user.getRole());
    }

    private static void requireAuthor(PostsDTO request, Long authorId) {
        if (request.getUserId() != null && !authorId.equals(request.getUserId())) {
            throw BusinessException.badRequest("作者由服务端确定，不能指定或变更为其他用户");
        }
    }

    private void requireCategory(Long id) {
        if (id != null && categoryMapper.selectById(id) == null) {
            throw BusinessException.notFound("分类不存在");
        }
    }

    private void requireUniqueSlug(String slug, Long excludedId) {
        // 已删除文章仍保留别名，与数据库全局唯一约束保持一致。
        if (postMapper.selectCount(Wrappers.<Posts>lambdaQuery()
                .eq(Posts::getSlug, slug).ne(excludedId != null, Posts::getId, excludedId)) > 0) {
            throw BusinessException.conflict("文章别名已存在");
        }
    }

    private static void applyFields(Posts post, PostsDTO request, LocalDateTime now) {
        post.setCategoryId(request.getCategoryId());
        post.setTitle(request.getTitle());
        post.setSlug(request.getSlug());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setCoverImage(request.getCoverImage());
        post.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        post.setIsTop(request.getIsTop() == null ? 0 : request.getIsTop());
        if (post.getStatus() == 1) {
            if (post.getPublishedAt() == null) {
                post.setPublishedAt(now);
            }
        } else {
            post.setPublishedAt(null);
        }
        post.setUpdatedAt(now);
    }

    private static PostsVO toVO(Posts post) {
        PostsVO result = new PostsVO();
        BeanUtils.copyProperties(post, result);
        return result;
    }
}
