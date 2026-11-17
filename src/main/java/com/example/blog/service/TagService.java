package com.example.blog.service;

import com.example.blog.dto.SearchListQuery;
import com.example.blog.dto.TagsDTO;
import com.example.blog.vo.PageResult;
import com.example.blog.vo.TagsVO;


/** 标签业务接口。 */
public interface TagService {

    TagsVO create(TagsDTO request);

    TagsVO update(Long id, TagsDTO request);

    void delete(Long id);

    TagsVO get(Long id);

    PageResult<TagsVO> list(SearchListQuery query);
}
