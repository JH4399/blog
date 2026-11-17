package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.common.PageQueries;
import com.example.blog.dto.CategoriesDTO;
import com.example.blog.dto.ListQuery;
import com.example.blog.entity.Categories;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.service.CategoryService;
import com.example.blog.vo.CategoriesVO;
import com.example.blog.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** 分类增删改查，唯一约束和外键约束由数据库兜底。 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoriesVO create(CategoriesDTO request) {
        if (request.getId() != null) {
            throw BusinessException.badRequest("新增分类时不能指定 ID");
        }
        requireUniqueSlug(request.getSlug(), null);
        Categories category = new Categories();
        applyFields(category, request);
        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        try {
            categoryMapper.insert(category);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "分类名称或别名已存在", exception);
        }
        return toVO(category);
    }

    @Override
    @Transactional
    public CategoriesVO update(Long id, CategoriesDTO request) {
        if (request.getId() != null && !id.equals(request.getId())) {
            throw BusinessException.badRequest("请求体 ID 必须与路径 ID 一致");
        }
        Categories category = requireCategory(id);
        requireUniqueSlug(request.getSlug(), id);
        applyFields(category, request);
        category.setUpdatedAt(LocalDateTime.now());
        try {
            if (categoryMapper.updateById(category) == 0) {
                throw BusinessException.notFound("分类不存在");
            }
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "分类名称或别名已存在", exception);
        }
        return toVO(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (categoryMapper.deleteById(id) == 0) {
                throw BusinessException.notFound("分类不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "分类存在关联数据，无法删除", exception);
        }
    }

    @Override
    public PageResult<CategoriesVO> list(ListQuery query) {
        String pattern = query.searchPattern();
        var wrapper = Wrappers.<Categories>lambdaQuery()
                .and(pattern != null, w -> w.apply("LOWER(\"name\") LIKE {0} ESCAPE '!' OR LOWER(\"slug\") LIKE {0} ESCAPE '!'", pattern))
                .orderByAsc(Categories::getSort, Categories::getId);
        return PageQueries.select(query, page -> categoryMapper.selectPage(page, wrapper), CategoryServiceImpl::toVO);
    }

    @Override
    public CategoriesVO get(Long id) {
        return toVO(requireCategory(id));
    }

    private Categories requireCategory(Long id) {
        Categories category = categoryMapper.selectById(id);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }
        return category;
    }

    private void requireUniqueSlug(String slug, Long excludedId) {
        if (categoryMapper.selectCount(Wrappers.<Categories>lambdaQuery()
                .eq(Categories::getSlug, slug)
                .ne(excludedId != null, Categories::getId, excludedId)) > 0) {
            throw BusinessException.conflict("分类别名已存在");
        }
    }

    private static void applyFields(Categories category, CategoriesDTO request) {
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setSort(request.getSort() == null ? 0 : request.getSort());
    }

    private static CategoriesVO toVO(Categories category) {
        CategoriesVO result = new CategoriesVO();
        result.setId(category.getId());
        result.setName(category.getName());
        result.setSlug(category.getSlug());
        result.setDescription(category.getDescription());
        result.setSort(category.getSort());
        result.setCreatedAt(category.getCreatedAt());
        result.setUpdatedAt(category.getUpdatedAt());
        return result;
    }
}
