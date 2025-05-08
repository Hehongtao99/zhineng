package com.boot.web.modules.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Resources;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 资源表 服务类
 * </p>
 */
public interface ResourcesService extends IService<Resources> {

    Page<Resources> search(Integer pageSize, Integer pageNum, Integer type, String searchKey);
}
