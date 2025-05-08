package com.boot.web.modules.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Category;
import com.boot.web.modules.web.mapper.CategoryMapper;
import com.boot.web.modules.web.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 类别表 服务实现类
 * </p>
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Page<Category> search(Integer pageSize, Integer pageNum) {
        Page<Category> page = new Page<>(pageNum, pageSize);
        return categoryMapper.selectPage(page, new QueryWrapper<>());
    }
}
