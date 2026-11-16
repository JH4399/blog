package com.example.blog.service;

import com.example.blog.dto.TagsDTO;
import com.example.blog.vo.TagsVO;

import java.util.List;

/** 标签业务接口。 */
public interface TagService {

    TagsVO create(TagsDTO request);

    TagsVO update(Long id, TagsDTO request);

    void delete(Long id);

    List<TagsVO> list();

    TagsVO get(Long id);
}
