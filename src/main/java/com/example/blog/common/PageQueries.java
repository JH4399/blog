package com.example.blog.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.dto.ListQuery;
import com.example.blog.vo.PageResult;

import java.util.function.Function;

/** 适配分页结果，并保留超出末页时回退的接口约定；COUNT 和分页 SQL 由插件生成。 */
public final class PageQueries {
    private PageQueries() {}

    public static <T> PageResult<T> select(ListQuery query, Function<Page<T>, IPage<T>> select) {
        return select(query, select, Function.identity());
    }

    public static <E, T> PageResult<T> select(ListQuery query, Function<Page<E>, IPage<E>> select,
            Function<E, T> toVO) {
        Page<E> request = new Page<>(query.getPage(), query.getSize());
        IPage<E> page = select.apply(request);
        long pages = Math.max(1, page.getPages());
        if (page.getTotal() == 0) {
            page.setCurrent(1);
        } else if (page.getCurrent() > pages) {
            // 删除后回退末页，只重新查询该页记录，不重复 COUNT。
            request.setCurrent(pages);
            request.setSearchCount(false);
            page = select.apply(request);
        }
        return new PageResult<>(page.getRecords().stream().map(toVO).toList(), page.getTotal(),
                Math.toIntExact(page.getCurrent()), Math.toIntExact(page.getSize()), pages);
    }
}
