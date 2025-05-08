package com.boot.web.modules.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Resources;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 资源表 Mapper 接口
 * </p>
 */
public interface ResourcesMapper extends BaseMapper<Resources> {

    Page<Resources> searchResources(Page<Resources> page, String searchKey, Integer type);
}
