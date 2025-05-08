package com.boot.web.modules.web.controller;


import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.ums.model.UmsAdmin;
import com.boot.web.modules.ums.service.UmsAdminService;
import com.boot.web.modules.web.service.TaskService;
import com.boot.web.modules.web.vo.Trend;
import com.boot.web.modules.web.vo.TrendPie;
import com.boot.web.security.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/web/chart")
public class ChartController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UmsAdminService adminService;

    @ApiOperation("任务状态统计数据")
    @GetMapping("/trendPie")
    public CommonResult<List<TrendPie>> trendPie() {
        UmsAdmin currentUser = SecurityUtils.getUser();
        if (currentUser == null) {
            return CommonResult.unauthorized(null);
        }
        
        // 检查是否是管理员
        boolean isAdmin = adminService.getRoleList(currentUser.getId()).stream()
                .anyMatch(role -> "管理员".equals(role.getName()) || "admin".equals(role.getName()) || "ADMIN".equals(role.getName().toUpperCase()));
        
        log.info("用户 [{}] 请求饼图数据，是否管理员: {}", currentUser.getUsername(), isAdmin);
        
        List<TrendPie> result;
        if (isAdmin) {
            // 管理员查看所有数据
            result = taskService.trendPie();
        } else {
            // 普通用户只查看与自己相关的数据
            result = taskService.trendPie(currentUser.getId());
        }
        
        log.info("饼图数据结果: {}", result);
        return CommonResult.success(result);
    }

    @ApiOperation("任务统计")
    @GetMapping("/trend")
    public CommonResult<List<Trend>> trend(@RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate){
        UmsAdmin currentUser = SecurityUtils.getUser();
        if (currentUser == null) {
            return CommonResult.unauthorized(null);
        }
        
        log.info("任务统计请求参数: startDate={}, endDate={}", startDate, endDate);
        
        // 如果日期为空，设置默认值
        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        // 检查是否是管理员
        boolean isAdmin = adminService.getRoleList(currentUser.getId()).stream()
                .anyMatch(role -> "管理员".equals(role.getName()) || "admin".equals(role.getName()) || "ADMIN".equals(role.getName().toUpperCase()));
        
        log.info("用户 [{}] 请求折线图数据，是否管理员: {}, 日期范围: {} 至 {}", 
                currentUser.getUsername(), isAdmin, startDate, endDate);
        
        List<Trend> result;
        if (isAdmin) {
            // 管理员查看所有数据
            result = taskService.trend(startDate, endDate);
        } else {
            // 普通用户只查看与自己相关的数据
            result = taskService.trend(startDate, endDate, currentUser.getId());
        }
        
        log.info("折线图数据结果: {}", result);
        return CommonResult.success(result);
    }

    @ApiOperation("用户完成任务统计")
    @GetMapping("/userTrend")
    public CommonResult<List<Trend>> userTrend(@RequestParam(value = "startDate", required = false) String startDate,
                                               @RequestParam(value = "endDate", required = false) String endDate){
        UmsAdmin currentUser = SecurityUtils.getUser();
        if (currentUser == null) {
            return CommonResult.unauthorized(null);
        }
        
        log.info("用户完成任务统计请求参数: userId={}, startDate={}, endDate={}", currentUser.getId(), startDate, endDate);
        
        // 如果日期为空，设置默认值
        if (startDate == null || startDate.isEmpty()) {
            startDate = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        if (endDate == null || endDate.isEmpty()) {
            endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        
        List<Trend> result = taskService.getUserCompletedTaskTrend(startDate, endDate, currentUser.getId());
        log.info("用户完成任务统计结果: {}", result);
        return CommonResult.success(result);
    }
}

