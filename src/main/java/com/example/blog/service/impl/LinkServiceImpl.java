package com.example.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.blog.dto.LinksDTO;
import com.example.blog.entity.Links;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.LinkMapper;
import com.example.blog.service.LinkService;
import com.example.blog.vo.LinksVO;
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
public class LinkServiceImpl implements LinkService {

    private final LinkMapper linkMapper;

    @Override
    @Transactional
    public LinksVO create(LinksDTO request) {
        if (request.getId() != null) {
            throw BusinessException.badRequest("新增友情链接时不能指定 ID");
        }
        Links link = new Links();
        applyFields(link, request);
        link.setCreatedAt(LocalDateTime.now());
        linkMapper.insert(link);
        return toVO(link);
    }

    @Override
    @Transactional
    public LinksVO update(Long id, LinksDTO request) {
        if (request.getId() != null && !id.equals(request.getId())) {
            throw BusinessException.badRequest("请求体 ID 必须与路径 ID 一致");
        }
        Links link = requireLink(id);
        applyFields(link, request);
        if (linkMapper.updateById(link) == 0) {
            throw BusinessException.notFound("友情链接不存在");
        }
        return toVO(link);
    }

    @Override
    public List<LinksVO> list(Integer status) {
        return linkMapper.selectList(Wrappers.<Links>lambdaQuery()
                        .eq(status != null, Links::getStatus, status)
                        .orderByAsc(Links::getSort, Links::getId))
                .stream().map(LinkServiceImpl::toVO).toList();
    }

    @Override
    public LinksVO get(Long id) {
        return toVO(requireLink(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            if (linkMapper.deleteById(id) == 0) {
                throw BusinessException.notFound("友情链接不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(HttpStatus.CONFLICT, "友情链接存在关联数据，无法删除", exception);
        }
    }

    private Links requireLink(Long id) {
        Links link = linkMapper.selectById(id);
        if (link == null) {
            throw BusinessException.notFound("友情链接不存在");
        }
        return link;
    }

    private static void applyFields(Links link, LinksDTO request) {
        link.setName(request.getName());
        link.setUrl(request.getUrl());
        link.setLogo(request.getLogo());
        link.setDescription(request.getDescription());
        link.setSort(request.getSort() == null ? 0 : request.getSort());
        link.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    private static LinksVO toVO(Links link) {
        LinksVO result = new LinksVO();
        BeanUtils.copyProperties(link, result);
        return result;
    }
}
