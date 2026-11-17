package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.dto.ListQuery;
import com.example.blog.dto.TagsDTO;
import com.example.blog.entity.Tags;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.TagMapper;
import com.example.blog.service.TagService;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.TagsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** 标签增删改查，唯一约束和外键约束由数据库兜底。 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    @Override
    @Transactional
    public TagsVO create(TagsDTO request) {
        if (request.getId() != null) {
            throw BusinessException.badRequest("新增标签时不能指定 ID");
        }
        requireUniqueSlug(request.getSlug(), null);
        Tags tag = new Tags();
        applyFields(tag, request);
        tag.setCreatedAt(LocalDateTime.now());
        try {
            tagMapper.insert(tag);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "标签名称或别名已存在", exception);
        }
        return toVO(tag);
    }

    @Override
    @Transactional
    public TagsVO update(Long id, TagsDTO request) {
        if (request.getId() != null && !id.equals(request.getId())) {
            throw BusinessException.badRequest("请求体 ID 必须与路径 ID 一致");
        }
        Tags tag = requireTag(id);
        requireUniqueSlug(request.getSlug(), id);
        applyFields(tag, request);
        try {
            if (tagMapper.updateById(tag) == 0) {
                throw BusinessException.notFound("标签不存在");
            }
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "标签名称或别名已存在", exception);
        }
        return toVO(tag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (tagMapper.deleteById(id) == 0) {
                throw BusinessException.notFound("标签不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "标签存在关联数据，无法删除", exception);
        }
    }

    @Override
    public PageResult<TagsVO> list(ListQuery query) {
        String keyword = query.getQ() == null ? "" : query.getQ().trim();
        var wrapper = Wrappers.<Tags>lambdaQuery()
                .and(!keyword.isEmpty(), w -> w.like(Tags::getName, keyword)
                        .or().like(Tags::getSlug, keyword))
                .orderByAsc(Tags::getId);
        var page = tagMapper.selectPage(new Page<Tags>(query.getPage(), query.getSize()), wrapper);
        var records = page.getRecords().stream().map(TagServiceImpl::toVO).toList();
        return new PageResult<>(records, page.getTotal(), query.getPage(), query.getSize(), page.getPages());
    }

    @Override
    public TagsVO get(Long id) {
        return toVO(requireTag(id));
    }

    private Tags requireTag(Long id) {
        Tags tag = tagMapper.selectById(id);
        if (tag == null) {
            throw BusinessException.notFound("标签不存在");
        }
        return tag;
    }

    private void requireUniqueSlug(String slug, Long excludedId) {
        if (tagMapper.selectCount(Wrappers.<Tags>lambdaQuery()
                .eq(Tags::getSlug, slug)
                .ne(excludedId != null, Tags::getId, excludedId)) > 0) {
            throw BusinessException.conflict("标签别名已存在");
        }
    }

    private static void applyFields(Tags tag, TagsDTO request) {
        tag.setName(request.getName());
        tag.setSlug(request.getSlug());
    }

    private static TagsVO toVO(Tags tag) {
        TagsVO result = new TagsVO();
        result.setId(tag.getId());
        result.setName(tag.getName());
        result.setSlug(tag.getSlug());
        result.setCreatedAt(tag.getCreatedAt());
        return result;
    }
}
