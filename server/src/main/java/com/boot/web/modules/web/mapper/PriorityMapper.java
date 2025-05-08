package com.boot.web.modules.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Priority;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 优先级表 Mapper 接口
 * </p>
 */
public interface PriorityMapper extends BaseMapper<Priority> {

    Page<Priority> searchPriority(Page<Priority> page, String searchKey, Integer type);
}
