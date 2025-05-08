package com.boot.web.modules.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boot.web.modules.web.vo.NotificationVo;

/**
 * <p>
 * 通知表 Mapper 接口
 * </p>
 */
public interface NotificationMapper extends BaseMapper<Notification> {

    Page<NotificationVo> search(Page<NotificationVo> page, String searchKey, Integer status);

    Page<NotificationVo> searchUser(Page<NotificationVo> page, String searchKey, Integer status, Long userId);
}
