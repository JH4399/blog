package com.example.blog.service;

import com.example.blog.dto.CategoriesDTO;
import com.example.blog.vo.CategoriesVO;

import java.util.List;

/** 分类业务接口。 */
public interface CategoryService {

    CategoriesVO create(CategoriesDTO request);

    CategoriesVO update(Long id, CategoriesDTO request);

    void delete(Long id);

    List<CategoriesVO> list();

    CategoriesVO get(Long id);
}
