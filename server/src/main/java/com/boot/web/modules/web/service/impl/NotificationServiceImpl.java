package com.boot.web.modules.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Notification;
import com.boot.web.modules.web.mapper.NotificationMapper;
import com.boot.web.modules.web.service.NotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.web.modules.web.vo.NotificationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Page<NotificationVo> search(Integer pageSize, Integer pageNum, Integer status, String searchKey) {
        Page<NotificationVo> page = new Page<>(pageNum, pageSize);
        return notificationMapper.search(page, searchKey, status);
    }

    @Override
    public Page<NotificationVo> searchUser(Integer pageSize, Integer pageNum, Integer status, String searchKey, Long userId) {
        Page<NotificationVo> page = new Page<>(pageNum, pageSize);
        return notificationMapper.searchUser(page, searchKey, status, userId);
    }
}
