package com.boot.web.modules.web.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.common.api.CommonPage;
import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Task;
import com.boot.web.modules.web.service.TaskService;
import com.boot.web.modules.web.vo.TaskVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/task")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @ApiOperation("根据条件查询列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<TaskVo>> list(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey) {
        Page<TaskVo> taskPage = taskService.search(pageSize, pageNum, categoryId, searchKey);

        return CommonResult.success(CommonPage.restPage(taskPage));
    }

    @ApiOperation(value = "创建任务")
    @PostMapping("/create")
    public CommonResult<Boolean> create(@RequestBody Task task) {
        return CommonResult.success(taskService.create(task));
    }

    @ApiOperation("修改任务")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Task task) {
        return CommonResult.success(taskService.updateTask(task));
    }

    @ApiOperation("修改任务状态")
    @PostMapping("/updateStatus")
    public CommonResult<Boolean> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        Task task = taskService.getById(id);
        if (task != null) {
            // 记录原始状态
            Integer oldStatus = task.getStatus();
            // 设置新状态
            task.setStatus(status);
            
            // 如果任务状态变为已完成(4)或待办(1)，且需要会议室，则自动释放会议室资源
            if ((status == 4 || status == 1) && task.getNeedMeetingRoom() != null && task.getNeedMeetingRoom()) {
                task.setNeedMeetingRoom(false);
            }
            
            boolean result = taskService.updateById(task);
            
            // 确保前端能立即看到状态变化
            if (result && !oldStatus.equals(status)) {
                taskService.updateTaskStatusInDB(task.getId(), status);
            }
            
            return CommonResult.success(result);
        }
        return CommonResult.failed("任务不存在");
    }

    @ApiOperation("设置任务提醒时间")
    @PostMapping("/setReminder")
    public CommonResult<Boolean> setReminder(@RequestParam Long id, @RequestParam(required = false) String reminderTime) {
        Task task = taskService.getById(id);
        if (task != null) {
            if (reminderTime != null && !reminderTime.isEmpty()) {
                // 使用DateTimeFormatter解析带空格的日期时间格式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                task.setReminderTime(java.time.LocalDateTime.parse(reminderTime, formatter));
            } else {
                task.setReminderTime(null); // 取消提醒
            }
            return CommonResult.success(taskService.updateById(task));
        }
        return CommonResult.failed("任务不存在");
    }

    @ApiOperation("移除任务")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Long id) {
        try {
            boolean success = taskService.deleteTaskWithSchedules(id);
            if (success) {
                return CommonResult.success(null);
            } else {
                return CommonResult.failed("任务不存在或删除失败");
            }
        } catch (Exception e) {
            log.error("删除任务失败", e);
            return CommonResult.failed("删除过程中发生错误: " + e.getMessage());
        }
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAllByStatus")
    public CommonResult<List<TaskVo>> listAll(@RequestParam(value = "status", required = false) Integer status) {
        return CommonResult.success(taskService.listAll(status));
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAll")
    public CommonResult<List<Task>> listAll() {
        return CommonResult.success(taskService.list());
    }

    @ApiOperation("查询任务详情")
    @GetMapping("/info/{id}")
    public CommonResult<TaskVo> getInfo(@PathVariable Long id) {
        TaskVo taskVo = taskService.getInfo(id);
        if (taskVo == null) {
            return CommonResult.failed("任务不存在或已被删除");
        }
        return CommonResult.success(taskVo);
    }

    @ApiOperation("手动触发指定日期的任务自动安排")
    @PostMapping("/autoArrangeByDate")
    public CommonResult<Boolean> autoArrangeByDate(@RequestParam String date) {
        try {
            // 解析日期字符串为LocalDate对象
            LocalDate targetDate = LocalDate.parse(date);
            boolean result = taskService.autoArrangeSchedules(targetDate);
            if (result) {
                return CommonResult.success(true, "任务已自动安排成功");
            } else {
                return CommonResult.failed("已安排部分任务，但部分任务超出了当日最大工作时间(18:00)。请调整任务时间或将其安排到其他日期。");
            }
        } catch (Exception e) {
            log.error("任务自动安排时发生错误", e);
            return CommonResult.failed("发生错误: " + e.getMessage());
        }
    }

    @ApiOperation("获取指定日期剩余可安排时间(分钟)")
    @GetMapping("/getRemainingTimeForDate")
    public CommonResult<Integer> getRemainingTimeForDate(@RequestParam String date) {
        try {
            // 解析日期字符串为LocalDate对象
            LocalDate targetDate = LocalDate.parse(date);
            int remainingMinutes = taskService.getRemainingTimeForDate(targetDate);
            return CommonResult.success(remainingMinutes);
        } catch (Exception e) {
            log.error("获取日期剩余时间时发生错误", e);
            return CommonResult.failed("发生错误: " + e.getMessage());
        }
    }

    @ApiOperation("获取指定用户在指定日期的剩余可安排时间(分钟)")
    @GetMapping("/getRemainingTimeForUserAndDate")
    public CommonResult<Integer> getRemainingTimeForUserAndDate(@RequestParam String date, @RequestParam Long userId) {
        try {
            // 解析日期字符串为LocalDate对象
            LocalDate targetDate = LocalDate.parse(date);
            int remainingMinutes = taskService.getRemainingTimeForUserAndDate(targetDate, userId);
            return CommonResult.success(remainingMinutes);
        } catch (Exception e) {
            log.error("获取用户在指定日期剩余时间时发生错误", e);
            return CommonResult.failed("发生错误: " + e.getMessage());
        }
    }

    @ApiOperation("检查任务时间冲突")
    @PostMapping("/checkTimeConflict")
    public CommonResult<List<TaskVo>> checkTimeConflict(@RequestBody Task task) {
        List<TaskVo> conflicts = taskService.checkTimeConflict(task);
        if (conflicts.isEmpty()) {
            return CommonResult.success(conflicts, "没有时间冲突");
        } else {
            return CommonResult.failed("存在时间冲突");
        }
    }
}

