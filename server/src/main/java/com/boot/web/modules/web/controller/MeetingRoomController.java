package com.boot.web.modules.web.controller;

import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Task;
import com.boot.web.modules.web.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 会议室 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/meetingRoom")
@Api(tags = "MeetingRoomController", description = "会议室管理")
public class MeetingRoomController {
    @Autowired
    private TaskService taskService;

    @ApiOperation("获取会议室总数")
    @GetMapping("/count")
    public CommonResult<Integer> getMeetingRoomCount() {
        // 默认会议室总数为5
        return CommonResult.success(5);
    }

    @ApiOperation("获取可用会议室数量")
    @GetMapping("/available")
    public CommonResult<Integer> getAvailableMeetingRoomCount() {
        // 总数默认为5
        int totalCount = 5;
        
        // 查询当前已占用的会议室数量
        int usedCount = taskService.getUsedMeetingRoomCount();
        
        // 计算可用数量
        int availableCount = Math.max(0, totalCount - usedCount);
        
        return CommonResult.success(availableCount);
    }
} 