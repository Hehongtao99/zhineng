package com.boot.web.modules.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Priority;
import com.boot.web.modules.web.mapper.PriorityMapper;
import com.boot.web.modules.web.service.PriorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优先级表 服务实现类
 * </p>
 */
@Service
public class PriorityServiceImpl extends ServiceImpl<PriorityMapper, Priority> implements PriorityService {

    @Autowired
    private PriorityMapper priorityMapper;

    @Override
    public Page<Priority> search(Integer pageSize, Integer pageNum, Integer type, String searchKey) {
        Page<Priority> page = new Page<>(pageNum, pageSize);
        return priorityMapper.searchPriority(page, searchKey, type);
    }
}
