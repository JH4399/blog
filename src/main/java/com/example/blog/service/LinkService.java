package com.example.blog.service;

import com.example.blog.dto.LinksDTO;
import com.example.blog.vo.LinksVO;

import java.util.List;

/** 友情链接业务接口。 */
public interface LinkService {

    LinksVO create(LinksDTO request);

    LinksVO update(Long id, LinksDTO request);

    List<LinksVO> list(Integer status);

    LinksVO get(Long id);

    void delete(Long id);
}
