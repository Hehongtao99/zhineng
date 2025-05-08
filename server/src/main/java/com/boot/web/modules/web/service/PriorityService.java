package com.boot.web.modules.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Priority;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 优先级表 服务类
 * </p>
 */
public interface PriorityService extends IService<Priority> {

    Page<Priority> search(Integer pageSize, Integer pageNum, Integer type, String searchKey);
}
