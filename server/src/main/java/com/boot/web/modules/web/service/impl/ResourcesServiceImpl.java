package com.boot.web.modules.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Resources;
import com.boot.web.modules.web.mapper.ResourcesMapper;
import com.boot.web.modules.web.service.ResourcesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资源表 服务实现类
 * </p>
 */
@Service
public class ResourcesServiceImpl extends ServiceImpl<ResourcesMapper, Resources> implements ResourcesService {

    @Autowired
    private ResourcesMapper resourcesMapper;

    @Override
    public Page<Resources> search(Integer pageSize, Integer pageNum, Integer type, String searchKey) {
        Page<Resources> page = new Page<>(pageNum, pageSize);
        return resourcesMapper.searchResources(page, searchKey, type);
    }
}
