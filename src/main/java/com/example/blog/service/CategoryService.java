package com.example.blog.service;

import com.example.blog.dto.CategoriesDTO;
import com.example.blog.dto.ListQuery;
import com.example.blog.vo.CategoriesVO;
import com.example.blog.vo.PageResult;


/** 分类业务接口。 */
public interface CategoryService {

    CategoriesVO create(CategoriesDTO request);

    CategoriesVO update(Long id, CategoriesDTO request);

    void delete(Long id);

    CategoriesVO get(Long id);

    PageResult<CategoriesVO> list(ListQuery query);
}
