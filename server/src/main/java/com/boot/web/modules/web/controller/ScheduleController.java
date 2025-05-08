package com.boot.web.modules.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.common.api.CommonPage;
import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Schedule;
import com.boot.web.modules.web.service.ScheduleService;
import com.boot.web.modules.web.vo.ScheduleVo;
import com.boot.web.modules.web.vo.TaskVo;
import com.boot.web.security.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日程安排表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/schedule")
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("根据条件查询列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<ScheduleVo>> list(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey,
                                                 @RequestParam(value = "status", required = false) Integer status,
                                                 @RequestParam(value = "userId", required = false) Long userId) {
        Page<ScheduleVo> schedulePage;
        
        if (userId != null) {
            // 如果指定了用户ID，则按用户ID查询
            schedulePage = scheduleService.searchUser(pageSize, pageNum, searchKey, userId, status);
        } else {
            // 否则查询所有
            schedulePage = scheduleService.search(pageSize, pageNum, searchKey, status);
        }

        return CommonResult.success(CommonPage.restPage(schedulePage));
    }

    @ApiOperation("根据条件查询列表 - 智能排序")
    @GetMapping("/smartSort")
    public CommonResult<CommonPage<ScheduleVo>> smartSort(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey,
                                                 @RequestParam(value = "userId", required = false) Long userId) {
        Page<ScheduleVo> schedulePage;
        
        if (userId != null) {
            // 如果指定了用户ID，则按用户ID进行智能排序
            schedulePage = scheduleService.smartSortUser(pageSize, pageNum, searchKey, userId);
        } else {
            // 否则对所有日程进行智能排序
            schedulePage = scheduleService.smartSort(pageSize, pageNum, searchKey);
        }

        return CommonResult.success(CommonPage.restPage(schedulePage));
    }

    @ApiOperation("用户日程查询 - 智能排序")
    @GetMapping("/user/smartSort")
    public CommonResult<CommonPage<ScheduleVo>> userSmartSort(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<ScheduleVo> schedulePage = scheduleService.smartSortUser(pageSize, pageNum, searchKey, userId);

        return CommonResult.success(CommonPage.restPage(schedulePage));
    }

    @ApiOperation(value = "创建日程安排")
    @PostMapping("/create")
    public CommonResult<Boolean> create(@RequestBody Schedule schedule) {
        return CommonResult.success(scheduleService.create(schedule));
    }

    @ApiOperation("修改日程安排")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Schedule schedule) {
        boolean success = scheduleService.updateSchedule(schedule);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改日程时间")
    @PostMapping("/updateTime")
    public CommonResult<Boolean> updateTime(@RequestParam Long id,
                                            @RequestParam String startTime,
                                            @RequestParam(required = false) String endTime) {
        Schedule schedule = scheduleService.getById(id);
        if (schedule != null) {
            schedule.setStartTime(LocalDateTime.parse(startTime));
            if (endTime != null && !endTime.isEmpty()) {
                schedule.setEndTime(LocalDateTime.parse(endTime));
            } else {
                schedule.setEndTime(null); // 将由系统根据任务时长自动计算
            }
            boolean success = scheduleService.updateById(schedule);
            if (success) {
                return CommonResult.success(true);
            }
        }
        return CommonResult.failed("修改失败，请稍后重试");
    }

    @ApiOperation("移除日程安排")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Long id) {
        Schedule schedule = scheduleService.getById(id);
        boolean success = scheduleService.removeById(schedule);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed("移除失败，请稍后重试");
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAll")
    public CommonResult<List<Schedule>> listAll() {
        List<Schedule> scheduleVos = scheduleService.list();

        return CommonResult.success(scheduleVos);
    }

    @ApiOperation("查询冲突列表")
    @GetMapping("/conflict/list/{id}")
    public CommonResult<List<TaskVo>> conflictList(@PathVariable Long id, @RequestParam(value = "scheduleIds") List<Long> scheduleIds) {
        return CommonResult.success(scheduleService.conflictList(id, scheduleIds));
    }

    @ApiOperation("根据条件查询列表")
    @GetMapping("/user/list")
    public CommonResult<CommonPage<ScheduleVo>> userList(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "searchKey", required = false) String searchKey,
                                                     @RequestParam(value = "status", required = false) Integer status) {
        Page<ScheduleVo> schedulePage = scheduleService.searchUser(pageSize, pageNum, searchKey, SecurityUtils.getUser().getId(), status);

        return CommonResult.success(CommonPage.restPage(schedulePage));
    }

    @ApiOperation("按月份查询日程安排")
    @GetMapping("/month/{yearMonth}")
    public CommonResult<Map<String, List<ScheduleVo>>> getSchedulesByMonth(@PathVariable String yearMonth) {
        return CommonResult.success(scheduleService.getSchedulesByMonth(yearMonth));
    }

    @ApiOperation("按月份查询基于用户权限的日程安排")
    @GetMapping("/month/user/{yearMonth}")
    public CommonResult<Map<String, List<ScheduleVo>>> getSchedulesByMonthAndUser(@PathVariable String yearMonth) {
        // 获取当前用户
        Long userId = SecurityUtils.getUser().getId();
        // 判断用户的角色，从ums_admin_role_relation表读取，roleId=1表示管理员
        boolean isAdmin = false;
        // 此处改为根据用户ID直接判断是否为1号用户（管理员）
        if (userId != null && userId == 1) {
            isAdmin = true;
        }

        return CommonResult.success(scheduleService.getSchedulesByMonthAndUser(yearMonth, userId, isAdmin));
    }

    @ApiOperation("调整日程顺序")
    @PostMapping("/adjustOrder")
    public CommonResult<Boolean> adjustScheduleOrder(@RequestParam Long scheduleId,
                                                   @RequestParam String targetTime,
                                                   @RequestParam String date) {
        try {
            // 使用DateTimeFormatter正确解析日期时间字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime newTargetTime = LocalDateTime.parse(targetTime, formatter);
            LocalDate scheduleDate = LocalDate.parse(date);
            
            boolean success = scheduleService.adjustScheduleOrder(scheduleId, newTargetTime, scheduleDate);
            
            if (success) {
                return CommonResult.success(true, "日程调整成功");
            } else {
                return CommonResult.failed("日程调整失败，可能超出工作时间限制");
            }
        } catch (Exception e) {
            log.error("调整日程顺序时发生错误", e);
            return CommonResult.failed("调整失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取所有用户的日程，按权重排序")
    @GetMapping("/allUsers")
    public CommonResult<CommonPage<ScheduleVo>> getAllUsersSchedulesByWeight(
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "searchKey", required = false) String searchKey,
            @RequestParam(value = "status", required = false) Integer status) {
        
        Page<ScheduleVo> schedulePage = scheduleService.getAllUsersSchedulesByWeight(pageSize, pageNum, searchKey, status);
        return CommonResult.success(CommonPage.restPage(schedulePage));
    }
}

