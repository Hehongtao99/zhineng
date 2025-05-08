package com.boot.web.modules.web.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.common.api.CommonPage;
import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Notification;
import com.boot.web.modules.web.service.NotificationService;
import com.boot.web.modules.web.vo.NotificationVo;
import com.boot.web.security.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 通知表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @ApiOperation("根据条件查询列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<NotificationVo>> list(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "status", required = false) Integer status,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey) {
        Page<NotificationVo> notificationPage = notificationService.search(pageSize, pageNum, status, searchKey);

        return CommonResult.success(CommonPage.restPage(notificationPage));
    }

    @ApiOperation("根据条件查询列表")
    @GetMapping("/user/list")
    public CommonResult<CommonPage<NotificationVo>> userList(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "status", required = false) Integer status,
                                                         @RequestParam(value = "searchKey", required = false) String searchKey) {
        Long userId = SecurityUtils.getUser().getId();
        Page<NotificationVo> notificationPage = notificationService.searchUser(pageSize, pageNum, status, searchKey, userId);

        return CommonResult.success(CommonPage.restPage(notificationPage));
    }

    @ApiOperation(value = "创建通知")
    @PostMapping("/create")
    public CommonResult<String> create(@RequestBody Notification notification) {
        boolean isSuccess = notificationService.save(notification);
        if (!isSuccess) {
            return CommonResult.failed();
        }
        return CommonResult.success(null);
    }

    @ApiOperation("修改通知")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Notification notification) {
        boolean success = notificationService.updateById(notification);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("移除通知")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        boolean success = notificationService.removeById(notification);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed("移除失败，请稍后重试");
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAll")
    public CommonResult<List<Notification>> listAll() {
        List<Notification> notificationVos = notificationService.list();

        return CommonResult.success(notificationVos);
    }

    @ApiOperation("标记通知为已读")
    @PostMapping("/read/{id}")
    public CommonResult<String> markAsRead(@PathVariable Long id) {
        // 获取通知对象
        Notification notification = notificationService.getById(id);
        if (notification == null) {
            return CommonResult.failed("通知不存在");
        }
        
        // 验证当前用户是否有权限修改此通知
        Long userId = SecurityUtils.getUser().getId();
        if (!notification.getUserId().equals(userId)) {
            return CommonResult.forbidden("无权限操作此通知");
        }
        
        // 更新状态为已读
        notification.setStatus(2); // 2表示已读
        boolean success = notificationService.updateById(notification);
        
        if (success) {
            return CommonResult.success("标记已读成功");
        } else {
            return CommonResult.failed("操作失败，请稍后重试");
        }
    }

    @ApiOperation("全部标记为已读")
    @PostMapping("/readAll")
    public CommonResult<String> markAllAsRead() {
        Long userId = SecurityUtils.getUser().getId();
        
        // 构建查询条件：当前用户的所有未读通知
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Notification> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("status", 1); // status=1表示未读
        
        List<Notification> unreadNotifications = notificationService.list(queryWrapper);
        
        if (unreadNotifications.isEmpty()) {
            return CommonResult.success("没有未读通知");
        }
        
        // 批量更新为已读状态
        unreadNotifications.forEach(notification -> notification.setStatus(2)); // 2表示已读
        boolean success = notificationService.updateBatchById(unreadNotifications);
        
        if (success) {
            return CommonResult.success("全部标记已读成功");
        } else {
            return CommonResult.failed("操作失败，请稍后重试");
        }
    }
}

