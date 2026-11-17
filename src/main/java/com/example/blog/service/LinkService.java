package com.example.blog.service;

import com.example.blog.dto.LinksDTO;
import com.example.blog.dto.LinkListQuery;
import com.example.blog.vo.LinksVO;
import com.example.blog.vo.PageResult;


/** 友情链接业务接口。 */
public interface LinkService {

    LinksVO create(LinksDTO request);

    LinksVO update(Long id, LinksDTO request);

    LinksVO get(Long id);

    void delete(Long id);
    PageResult<LinksVO> list(LinkListQuery query);
}
