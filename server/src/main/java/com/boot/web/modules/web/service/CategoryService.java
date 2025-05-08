package com.boot.web.modules.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 类别表 服务类
 * </p>
 */
public interface CategoryService extends IService<Category> {

    Page<Category> search(Integer pageSize, Integer pageNum);
}
