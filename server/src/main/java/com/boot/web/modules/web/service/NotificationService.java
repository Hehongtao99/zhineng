package com.boot.web.modules.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Notification;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.web.modules.web.vo.NotificationVo;

/**
 * <p>
 * 通知表 服务类
 * </p>
 */
public interface NotificationService extends IService<Notification> {

    Page<NotificationVo> search(Integer pageSize, Integer pageNum, Integer status, String searchKey);

    Page<NotificationVo> searchUser(Integer pageSize, Integer pageNum, Integer status, String searchKey, Long userId);
}
