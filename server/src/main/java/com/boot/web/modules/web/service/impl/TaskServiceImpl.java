package com.boot.web.modules.web.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.ums.model.UmsAdmin;
import com.boot.web.modules.ums.service.UmsAdminService;
import com.boot.web.modules.web.model.Category;
import com.boot.web.modules.web.model.Priority;
import com.boot.web.modules.web.model.Resources;
import com.boot.web.modules.web.model.Task;
import com.boot.web.modules.web.model.Schedule;
import com.boot.web.modules.web.model.Notification;
import com.boot.web.modules.web.mapper.TaskMapper;
import com.boot.web.modules.web.mapper.PriorityMapper;
import com.boot.web.modules.web.service.CategoryService;
import com.boot.web.modules.web.service.PriorityService;
import com.boot.web.modules.web.service.ResourcesService;
import com.boot.web.modules.web.service.TaskService;
import com.boot.web.modules.web.service.ScheduleService;
import com.boot.web.modules.web.service.NotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.web.modules.web.vo.TaskVo;
import com.boot.web.modules.web.vo.Trend;
import com.boot.web.modules.web.vo.TrendPie;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 */
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private PriorityMapper priorityMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PriorityService priorityService;

    @Autowired
    private UmsAdminService userService;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    @Lazy
    private ScheduleService scheduleService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Page<TaskVo> search(Integer pageSize, Integer pageNum, Long categoryId, String searchKey) {
        // 先更新所有任务状态
        updateAllTaskStatus();

        Page<TaskVo> page = new Page<>(pageNum, pageSize);
        return taskMapper.searchTask(page, searchKey, categoryId);
    }

    @Override
    public List<TaskVo> listAll(Integer status) {
        // 先更新所有任务状态
        updateAllTaskStatus();

        return taskMapper.listAll(status);
    }

    @Override
    public TaskVo getInfo(Long id) {
        // 更新当前任务状态
        Task task = this.getById(id);
        if (task == null) {
            return null;
        }
        
        updateTaskStatus(task);

        TaskVo taskVo = new TaskVo();
        BeanUtils.copyProperties(task, taskVo);
        
        // 添加分类名称
        if (task.getCategoryId() != null) {
            Category category = categoryService.getById(task.getCategoryId());
            if (category != null) {
                taskVo.setCategoryName(category.getName());
            }
        }
        
        // 添加重要程度名称
        if (task.getImportanceId() != null) {
            Priority importancePriority = priorityService.getById(task.getImportanceId());
            if (importancePriority != null) {
                taskVo.setImportanceName(importancePriority.getName());
            }
        }
        
        // 添加紧急程度名称
        if (task.getExigencyId() != null) {
            Priority exigencyPriority = priorityService.getById(task.getExigencyId());
            if (exigencyPriority != null) {
                taskVo.setExigencyName(exigencyPriority.getName());
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);

        // 处理userList，防止空集合导致SQL错误
        List<UmsAdmin> userList = new ArrayList<>();
        if (!userIds.isEmpty()) {
            userList = userService.list(new QueryWrapper<UmsAdmin>().lambda().in(UmsAdmin::getId, userIds));
        }
        
        taskVo.setUserList(userList);

        return taskVo;
    }

    @Override
    public List<TrendPie> trendPie() {
        return taskMapper.trendPie(null);
    }

    @Override
    public List<TrendPie> trendPie(Long userId) {
        return taskMapper.trendPie(userId);
    }

    @Override
    public List<Trend> trend(String startDate, String endDate) {
        return taskMapper.trend(startDate, endDate, null);
    }

    @Override
    public List<Trend> trend(String startDate, String endDate, Long userId) {
        return taskMapper.trend(startDate, endDate, userId);
    }

    @Override
    public List<Trend> getUserCompletedTaskTrend(String startDate, String endDate, Long userId) {
        return taskMapper.userCompletedTaskTrend(startDate, endDate, userId);
    }

    @Override
    public Boolean create(Task task) {
        task.setCode(IdUtil.randomUUID());
        List<Priority> priorities = priorityService.list();

        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 如果设置了startTime，将时间部分设置为当天的00:00:00
        if (task.getStartTime() != null) {
            // LocalDate不需要处理时间部分，因为它本身就只是日期
            // task.setStartTime(task.getStartTime().toLocalDate().atStartOfDay());
        }

        // 遍历优先级数据，设置 importanceId 和 exigencyId
        for (Priority priority : priorities) {
            // 匹配 importanceId (type=1 且 categoryId 相同)
            if (priority.getType() == 1 && priority.getCategoryId() != null && priority.getCategoryId().equals(task.getCategoryId())) {
                task.setImportanceId(priority.getId());
            }

            // 匹配 exigencyId (type=2 且 deadline 在时间区间范围内)
            if (priority.getType() == 2 && task.getDeadline() != null) {
                long hoursDifference = Duration.between(now, task.getDeadline()).toHours();
                
                // 修复紧急性设置逻辑
                // 如果距离截止时间不足48小时（2天），强制设为高紧急性
                if (hoursDifference <= 48) {
                    // 检查这个priority是否是"高"紧急性
                    if (priority.getName() != null && priority.getName().equals("高")) {
                        task.setExigencyId(priority.getId());
                        // 一旦找到匹配的高紧急性，立即中断循环
                        break;
                    }
                } else {
                    // 超过48小时的情况，使用原来的逻辑
                    if (priority.getMinHours() == null) {
                        priority.setMinHours(0);
                    }
                    if (priority.getMaxHours() == null) {
                        priority.setMaxHours(0);
                    }

                    // 如果最大值为 0，则视为正无穷
                    boolean withinRange = false;
                    if (priority.getMinHours() != null && priority.getMaxHours() != null) {
                        if (hoursDifference >= priority.getMinHours() && (priority.getMaxHours() == 0 || hoursDifference <= priority.getMaxHours())) {
                            withinRange = true;
                        }
                    } else if (priority.getMinHours() != null && priority.getMaxHours() == null) {
                        if (hoursDifference >= priority.getMinHours()) {
                            withinRange = true;
                        }
                    }

                    if (withinRange) {
                        task.setExigencyId(priority.getId());
                    }
                }
            }
        }

        // 设置任务初始状态为待办
        if (task.getStatus() == null) {
            task.setStatus(1);
        }
        
        // 如果没有设置needMeetingRoom字段，默认为不需要会议室
        if (task.getNeedMeetingRoom() == null) {
            task.setNeedMeetingRoom(false);
        }
        
        // 保存任务
        boolean success = this.save(task);
        
        // 如果任务保存成功，调用自动安排日程功能
        if (success && task.getId() != null && task.getStartTime() != null) {
            // 获取任务的开始日期
            LocalDate taskDate = task.getStartTime();
            
            // 自动安排该日期的所有任务
            autoArrangeSchedules(taskDate);
        }
        
        return success;
    }

    @Override
    public Boolean updateTask(Task task) {
        // 自动设置优先级权重
        List<Priority> priorities = priorityService.list();

        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 如果设置了startTime，将时间部分设置为当天的00:00:00
        if (task.getStartTime() != null) {
            // LocalDate不需要处理时间部分，因为它本身就只是日期
            // task.setStartTime(task.getStartTime().toLocalDate().atStartOfDay());
        }

        // 遍历优先级数据，设置 importanceId 和 exigencyId
        for (Priority priority : priorities) {
            // 匹配 importanceId (type=1 且 categoryId 相同)
            if (priority.getType() == 1 && priority.getCategoryId() != null && priority.getCategoryId().equals(task.getCategoryId())) {
                task.setImportanceId(priority.getId());
            }

            // 匹配 exigencyId (type=2 且 deadline 在时间区间范围内)
            if (priority.getType() == 2 && task.getDeadline() != null) {
                long hoursDifference = Duration.between(now, task.getDeadline()).toHours();
                
                // 修复紧急性设置逻辑
                // 如果距离截止时间不足48小时（2天），强制设为高紧急性
                if (hoursDifference <= 48) {
                    // 检查这个priority是否是"高"紧急性
                    if (priority.getName() != null && priority.getName().equals("高")) {
                        task.setExigencyId(priority.getId());
                        // 一旦找到匹配的高紧急性，立即中断循环
                        break;
                    }
                } else {
                    // 超过48小时的情况，使用原来的逻辑
                    if (priority.getMinHours() == null) {
                        priority.setMinHours(0);
                    }
                    if (priority.getMaxHours() == null) {
                        priority.setMaxHours(0);
                    }

                    // 如果最大值为 0，则视为正无穷
                    boolean withinRange = false;
                    if (priority.getMinHours() != null && priority.getMaxHours() != null) {
                        if (hoursDifference >= priority.getMinHours() && (priority.getMaxHours() == 0 || hoursDifference <= priority.getMaxHours())) {
                            withinRange = true;
                        }
                    } else if (priority.getMinHours() != null && priority.getMaxHours() == null) {
                        if (hoursDifference >= priority.getMinHours()) {
                            withinRange = true;
                        }
                    }

                    if (withinRange) {
                        task.setExigencyId(priority.getId());
                    }
                }
            }
        }

        // 如果手动将任务设置为已完成，保持该状态
        Task oldTask = this.getById(task.getId());
        
        // 确保needMeetingRoom字段有值
        if (task.getNeedMeetingRoom() == null) {
            // 如果是更新操作且没有提供新值，则保持原值
            if (oldTask != null) {
                task.setNeedMeetingRoom(oldTask.getNeedMeetingRoom());
            } else {
                task.setNeedMeetingRoom(false);
            }
        }
        
        if (oldTask != null && oldTask.getStatus() != 4 && task.getStatus() != 4) {
            // 自动更新任务状态
            updateTaskStatus(task);
        }

        // 更新任务
        boolean success = this.updateById(task);
        
        // 如果任务更新成功，同步更新相关日程
        if (success && task.getId() != null) {
            try {
                // 查找与该任务关联的所有日程
                List<Schedule> schedules = scheduleService.list(
                    new QueryWrapper<Schedule>().eq("task_id", task.getId())
                );
                
                if (!schedules.isEmpty()) {
                    // 更新所有相关日程的时间
                    for (Schedule schedule : schedules) {
                        boolean needUpdate = false;
                        
                        // 如果任务的开始时间有更新，同步更新日程
                        if (task.getStartTime() != null && (schedule.getStartTime() == null || 
                               !task.getStartTime().atStartOfDay().equals(schedule.getStartTime()))) {
                            schedule.setStartTime(task.getStartTime().atStartOfDay());
                            needUpdate = true;
                        }
                        
                        // 更新结束时间
                        if (needUpdate || task.getTimeSpend() != null || task.getDeadline() != null) {
                            if (task.getTimeSpend() != null && schedule.getStartTime() != null) {
                                // 根据任务预估耗时重新计算结束时间
                                schedule.setEndTime(schedule.getStartTime().plusMinutes(task.getTimeSpend()));
                                needUpdate = true;
                            } else if (task.getDeadline() != null) {
                                // 使用截止时间作为结束时间
                                schedule.setEndTime(task.getDeadline());
                                needUpdate = true;
                            }
                        }
                        
                        // 如果需要更新，保存日程
                        if (needUpdate) {
                            scheduleService.updateById(schedule);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("同步更新日程失败: " + e.getMessage());
            }
        }

        return success;
    }

    /**
     * 每天凌晨执行一次，自动更新所有任务的状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledUpdateAllTaskStatus() {
        updateAllTaskStatus();
    }

    /**
     * 更新所有任务的状态
     */
    private void updateAllTaskStatus() {
        List<Task> allTasks = this.list();
        List<Task> tasksToUpdate = new ArrayList<>();
        
        for (Task task : allTasks) {
            // 保存原始状态
            Integer oldStatus = task.getStatus();
            Boolean oldNeedMeetingRoom = task.getNeedMeetingRoom();
            
            // 更新任务状态
            updateTaskStatus(task);
            
            // 如果状态或会议室需求有变化，则添加到待更新列表
            if (!Objects.equals(oldStatus, task.getStatus()) || 
                !Objects.equals(oldNeedMeetingRoom, task.getNeedMeetingRoom())) {
                tasksToUpdate.add(task);
            }
        }
        
        // 批量更新有变化的任务
        if (!tasksToUpdate.isEmpty()) {
            this.updateBatchById(tasksToUpdate);
            log.info("定时任务自动更新了 " + tasksToUpdate.size() + " 个任务的状态");
        }
    }

    /**
     * 根据任务的截止时间自动更新任务状态
     * 任务状态：1-待办, 4-已完成, 5-已逾期
     */
    private void updateTaskStatus(Task task) {
        // 如果任务已经是已完成状态(4)，不做改变
        if (task.getStatus() == 4) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean statusChanged = false;
        Integer oldStatus = task.getStatus();

        // 查找任务对应的日程安排
        List<Schedule> schedules = scheduleService.list(
            new QueryWrapper<Schedule>().eq("task_id", task.getId())
        );

        // 如果有日程安排，优先使用日程的结束时间来判断是否逾期
        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                // 检查是否已逾期（结束时间已过）
                if (schedule.getEndTime() != null && now.isAfter(schedule.getEndTime())) {
                    task.setStatus(5); // 已逾期
                    statusChanged = true;
                    break;
                }
            }
        } 
        // 如果没有日程安排，则使用任务的开始时间和预估耗时判断
        else if (task.getStartTime() != null && task.getTimeSpend() != null) {
            // 计算任务的结束时间：开始日期 + 预估耗时（从当天早上8点开始计算）
            LocalDateTime expectedEndTime = task.getStartTime().atTime(8, 0).plusMinutes(task.getTimeSpend());
            // 如果当前时间已超过预期结束时间，则设为已逾期
            if (now.isAfter(expectedEndTime)) {
                task.setStatus(5); // 已逾期
                statusChanged = true;
            }
        }
        // 如果没有日程安排，也没有开始时间和预估耗时，则使用任务的截止时间判断
        else if (task.getDeadline() != null && now.isAfter(task.getDeadline())) {
            task.setStatus(5); // 已逾期
            statusChanged = true;
        }

        // 如果状态未变更且不是待办(1)，则设为待办
        if (!statusChanged && task.getStatus() != 1 && task.getStatus() != 5) {
            task.setStatus(1); // 待办
        }
        
        // 如果任务状态变为已完成(4)或待办(1)，自动释放会议室资源
        if ((task.getStatus() == 4 || task.getStatus() == 1) && 
            !Objects.equals(oldStatus, task.getStatus()) && 
            task.getNeedMeetingRoom() != null && task.getNeedMeetingRoom()) {
            
            // 释放会议室资源
            task.setNeedMeetingRoom(false);
            log.info("任务ID: " + task.getId() + " 状态变更为: " + task.getStatus() + "，自动释放会议室资源");
        }
    }

    // 辅助方法：将 JSON 数组字符串解析为 Set<Long>
    private Set<Long> parseJsonArrayToSet(String jsonArray, ObjectMapper objectMapper) {
        try {
            if (jsonArray == null || jsonArray.isEmpty()) {
                return Collections.emptySet();
            }
            return new HashSet<>(Arrays.asList(objectMapper.readValue(jsonArray, Long[].class)));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    /**
     * 获取优先级Mapper
     * 
     * @return PriorityMapper实例
     */
    @Override
    public PriorityMapper getPriorityMapper() {
        return priorityMapper;
    }

    @Override
    public List<Long> findPendingMeetingRoomIds(LocalDateTime startTime, LocalDateTime endTime) {
        // 查找在指定时间范围内已分配会议室但尚未创建日程的任务的会议室ID列表
        return taskMapper.findPendingMeetingRoomIds(startTime, endTime);
    }
    
    @Override
    public int getUsedMeetingRoomCount() {
        // 查询当前需要会议室的任务数量
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Task::getNeedMeetingRoom, true)
                .eq(Task::getStatus, 1); // 只统计状态为待办的任务
        
        return (int) this.count(queryWrapper);
    }
    
    @Override
    public boolean updateTaskStatusInDB(Long taskId, Integer status) {
        Task task = this.getById(taskId);
        if (task == null) {
            return false;
        }
        
        // 记录原状态
        Integer oldStatus = task.getStatus();
        
        // 设置新状态
        task.setStatus(status);
        
        // 如果任务状态变为已完成(4)或待办(1)，且需要会议室，则自动释放会议室资源
        if ((status == 4 || status == 1) && 
            task.getNeedMeetingRoom() != null && task.getNeedMeetingRoom()) {
            task.setNeedMeetingRoom(false);
            log.info("任务ID: " + task.getId() + " 状态手动更新为: " + status + "，自动释放会议室资源");
        }
        
        // 确保状态更新会立即保存到数据库
        return this.updateById(task);
    }

    @Override
    public boolean deleteTaskWithSchedules(Long taskId) {
        // 验证任务是否存在
        Task task = this.getById(taskId);
        if (task == null) {
            log.warn("尝试删除不存在的任务: ID {}", taskId);
            return false;
        }
        
        try {
            // 开启事务，确保任务和关联日程一起删除或都不删除
            // 步骤1: 删除关联的所有日程
            scheduleService.remove(new QueryWrapper<Schedule>().eq("task_id", taskId));
            log.info("已删除任务ID: {} 的所有关联日程", taskId);
            
            // 步骤2: 删除任务本身
            boolean removed = this.removeById(taskId);
            if (removed) {
                log.info("已成功删除任务: ID {}", taskId);
                return true;
            } else {
                log.error("删除任务失败: ID {}", taskId);
                return false;
            }
        } catch (Exception e) {
            log.error("删除任务及其关联日程时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 自动安排指定日期的所有任务日程
     * 按照任务权重从高到低排序，从早上8点开始安排
     * 
     * @param date 需要安排的日期
     * @return 是否安排成功
     */
    public boolean autoArrangeSchedules(LocalDate date) {
        // 查找指定日期的所有任务（startTime为该日期的任务）
        List<Task> tasksForDate = this.list(new QueryWrapper<Task>()
                .eq("start_time", date.toString())
                .eq("status", 1)); // 只安排待办状态的任务
        
        if (tasksForDate.isEmpty()) {
            log.info("日期 {} 没有需要安排的任务", date);
            return true;
        }
        
        // 计算每个任务的优先级分数
        Map<Long, Double> taskPriorityScores = new HashMap<>();
        for (Task task : tasksForDate) {
            double priorityScore = calculateTaskPriorityScore(task);
            taskPriorityScores.put(task.getId(), priorityScore);
            log.info("任务 ID: {}, 标题: {}, 权重分数: {}", task.getId(), task.getTitle(), priorityScore);
        }
        
        // 根据优先级分数从高到低排序任务
        tasksForDate.sort((a, b) -> {
            Double scoreA = taskPriorityScores.getOrDefault(a.getId(), 0.0);
            Double scoreB = taskPriorityScores.getOrDefault(b.getId(), 0.0);
            return Double.compare(scoreB, scoreA); // 分数高的排在前面
        });
        
        // 设置开始时间为当天早上8点
        LocalDateTime dayStartTime = date.atTime(8, 0);
        
        // 设置每日最大工作时间为18点
        LocalDateTime dayEndTime = date.atTime(18, 0);
        
        // 跟踪未能安排的任务
        List<Task> unscheduledTasks = new ArrayList<>();

        // 跟踪每个用户当前的时间点
        Map<Long, LocalDateTime> userCurrentTimes = new HashMap<>();
        
        // 跟踪每个用户已安排的时间（分钟）
        Map<Long, Integer> userScheduledMinutes = new HashMap<>();
        
        // 总可用时间(分钟) = 10小时 = 600分钟
        final int totalAvailableMinutes = 600;
        
        // 按优先级顺序为每个任务创建日程
        for (Task task : tasksForDate) {
            // 计算当前任务的预估完成时间
            Integer timeSpend = task.getTimeSpend() != null ? task.getTimeSpend() : 60; // 默认1小时
            
            // 获取任务的参与用户
            Set<Long> userIds = new HashSet<>();
            if (task.getUserData() != null && !task.getUserData().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Long[] taskUserIds = objectMapper.readValue(task.getUserData(), Long[].class);
                    userIds.addAll(Arrays.asList(taskUserIds));
                } catch (Exception e) {
                    log.error("解析任务用户数据失败: " + e.getMessage());
                }
            }
            
            // 如果没有指定用户，添加到未安排列表
            if (userIds.isEmpty()) {
                unscheduledTasks.add(task);
                continue;
            }
            
            // 检查任务所有参与用户是否都有足够的时间
            boolean canScheduleForAllUsers = true;
            
            // 检查每个用户的可用时间
            for (Long userId : userIds) {
                // 初始化用户当前时间和已安排时间
                if (!userCurrentTimes.containsKey(userId)) {
                    userCurrentTimes.put(userId, dayStartTime);
                    
                    // 查询该用户在该日期已安排的所有日程
                    List<Schedule> userSchedules = scheduleService.list(new QueryWrapper<Schedule>()
                            .eq("DATE(start_time)", date.toString())
                            .eq("user_id", userId));
                    
                    int scheduledMinutes = 0;
                    for (Schedule schedule : userSchedules) {
                        // 确保日程在工作时间内(8:00-18:00)
                        LocalDateTime scheduleStartTime = schedule.getStartTime().isBefore(dayStartTime) ? 
                                dayStartTime : schedule.getStartTime();
                        LocalDateTime scheduleEndTime = schedule.getEndTime().isAfter(dayEndTime) ? 
                                dayEndTime : schedule.getEndTime();
                        
                        // 计算日程持续时间(分钟)
                        if (scheduleStartTime.isBefore(scheduleEndTime)) {
                            long durationMinutes = Duration.between(scheduleStartTime, scheduleEndTime).toMinutes();
                            scheduledMinutes += durationMinutes;
                        }
                    }
                    
                    userScheduledMinutes.put(userId, scheduledMinutes);
                }
                
                // 获取用户当前时间点和已安排时间
                LocalDateTime userCurrentTime = userCurrentTimes.get(userId);
                int userMinutesScheduled = userScheduledMinutes.get(userId);
                
                // 检查用户是否有足够的时间
                if (userMinutesScheduled + timeSpend > totalAvailableMinutes) {
                    canScheduleForAllUsers = false;
                    log.warn("用户ID: {} 没有足够的时间安排任务ID: {}, 标题: {}, 已安排: {}分钟, 需要: {}分钟",
                            userId, task.getId(), task.getTitle(), userMinutesScheduled, timeSpend);
                    break;
                }
                
                // 检查用户的日程是否会超过工作时间
                LocalDateTime userEndTime = userCurrentTime.plusMinutes(timeSpend);
                if (userEndTime.isAfter(dayEndTime)) {
                    canScheduleForAllUsers = false;
                    log.warn("用户ID: {} 的日程会超过工作时间限制, 任务ID: {}, 标题: {}",
                            userId, task.getId(), task.getTitle());
                    break;
                }
            }
            
            // 如果无法为所有用户安排该任务，则添加到未安排列表
            if (!canScheduleForAllUsers) {
                unscheduledTasks.add(task);
                continue;
            }
            
            // 删除之前可能存在的日程
            scheduleService.remove(new QueryWrapper<Schedule>().eq("task_id", task.getId()));
            
            // 创建父日程（管理员查看）- 使用最早的用户开始时间
            LocalDateTime earliestStartTime = userIds.stream()
                    .map(userCurrentTimes::get)
                    .min(LocalDateTime::compareTo)
                    .orElse(dayStartTime);
            
            Schedule parentSchedule = new Schedule();
            parentSchedule.setTaskId(task.getId());
            parentSchedule.setStartTime(earliestStartTime);
            parentSchedule.setEndTime(earliestStartTime.plusMinutes(timeSpend));
            
            // 保存父日程
            scheduleService.save(parentSchedule);
            
            // 获取父日程ID
            Long parentId = parentSchedule.getId();
            
            // 为每个参与用户创建子日程并更新他们的时间
            for (Long userId : userIds) {
                // 获取用户当前时间点
                LocalDateTime userCurrentTime = userCurrentTimes.get(userId);
                
                // 创建用户的子日程
                Schedule childSchedule = new Schedule();
                childSchedule.setTaskId(task.getId());
                childSchedule.setStartTime(userCurrentTime);
                childSchedule.setEndTime(userCurrentTime.plusMinutes(timeSpend));
                childSchedule.setParentId(parentId);
                childSchedule.setUserId(userId);
                
                scheduleService.save(childSchedule);
                
                // 更新用户的当前时间点
                userCurrentTimes.put(userId, childSchedule.getEndTime());
                
                // 更新用户已安排的分钟数
                userScheduledMinutes.put(userId, userScheduledMinutes.get(userId) + timeSpend);
            }
            
            // 保持任务状态为待办
            task.setStatus(1);
            this.updateById(task);
        }
        
        // 如果有未能安排的任务，记录警告日志
        if (!unscheduledTasks.isEmpty()) {
            StringBuilder warningMsg = new StringBuilder();
            warningMsg.append("以下任务无法安排：\n");
            
            for (Task task : unscheduledTasks) {
                warningMsg.append(String.format("- 任务ID: %d, 标题: %s, 预估耗时: %d分钟\n", 
                                                task.getId(), task.getTitle(), 
                                                task.getTimeSpend() != null ? task.getTimeSpend() : 60));
            }
            
            log.warn(warningMsg.toString());
            
            // 返回false表示部分任务未能安排
            return false;
        }
        
        log.info("日期 {} 的任务日程已全部自动安排完成", date);
        return true;
    }

    /**
     * 计算任务的优先级分数
     */
    private double calculateTaskPriorityScore(Task task) {
        double importanceScore = 0.0;
        double urgencyScore = 0.0;
        
        // 根据任务的重要性计算得分 (importanceId)
        if (task.getImportanceId() != null) {
            // 查询重要性权重
            QueryWrapper<Priority> importanceQuery = new QueryWrapper<>();
            importanceQuery.eq("id", task.getImportanceId());
            Priority importance = priorityMapper.selectOne(importanceQuery);

            if (importance != null) {
                int score = importance.getScore() != null ? importance.getScore() : 1;
                BigDecimal weight = importance.getWeight() != null ? importance.getWeight() : BigDecimal.ONE;
                importanceScore = score * weight.doubleValue() * 10; // 放大10倍，使得分值更明显
            }
        }

        // 根据任务的紧急性计算得分 (exigencyId)
        if (task.getExigencyId() != null) {
            // 查询紧急性权重
            QueryWrapper<Priority> urgencyQuery = new QueryWrapper<>();
            urgencyQuery.eq("id", task.getExigencyId());
            Priority urgency = priorityMapper.selectOne(urgencyQuery);

            if (urgency != null) {
                int score = urgency.getScore() != null ? urgency.getScore() : 1;
                BigDecimal weight = urgency.getWeight() != null ? urgency.getWeight() : BigDecimal.ONE;
                urgencyScore = score * weight.doubleValue() * 10; // 放大10倍，使得分值更明显
            }
        }
        
        // 计算截止时间的紧急性
        double deadlineUrgency = 0.0;
        if (task.getDeadline() != null) {
            LocalDateTime now = LocalDateTime.now();

            // 如果已经超过截止时间，则紧急性最高
            if (now.isAfter(task.getDeadline())) {
                deadlineUrgency = 100.0;
            } else {
                // 计算距离截止时间还有多少小时
                long hoursUntilDeadline = Duration.between(now, task.getDeadline()).toHours();

                // 距离截止时间越近，紧急性越高
                // 改进紧急性计算逻辑
                // 24小时内：最高紧急性
                // 24-48小时：高紧急性 
                // 48-72小时：中高紧急性
                // 72小时以上：根据时间衰减
                if (hoursUntilDeadline <= 24) {
                    deadlineUrgency = 50.0; // 24小时内最高紧急性
                } else if (hoursUntilDeadline <= 48) {
                    deadlineUrgency = 40.0; // 48小时内高紧急性
                } else if (hoursUntilDeadline <= 72) {
                    deadlineUrgency = 30.0; // 72小时内中高紧急性
                } else {
                    // 超过72小时，紧急性随时间衰减，但设置最低值为5
                    deadlineUrgency = Math.max(5.0, 20.0 - ((hoursUntilDeadline - 72) / 72.0) * 10.0);
                }
            }
        }
        
        // 综合计算最终优先级得分
        double priorityScore = importanceScore + urgencyScore + deadlineUrgency;
        
        return Math.max(0, priorityScore); // 确保得分不为负数
    }

    /**
     * 获取指定日期剩余可安排时间(分钟)
     * @param date 指定日期
     * @return 剩余可安排分钟数
     */
    @Override
    public int getRemainingTimeForDate(LocalDate date) {
        // 工作日的开始时间(8:00)和结束时间(18:00)
        LocalDateTime dayStartTime = date.atTime(8, 0);
        LocalDateTime dayEndTime = date.atTime(18, 0);
        
        // 总可用时间(分钟) = 10小时 = 600分钟
        int totalAvailableMinutes = 600;
        
        // 查询该日期已安排的所有日程
        List<Schedule> schedulesForDate = scheduleService.list(new QueryWrapper<Schedule>()
                .eq("DATE(start_time)", date.toString())
                .isNull("parent_id")); // 只统计主日程，不统计子日程
        
        // 累计已安排的时间(分钟)
        int totalScheduledMinutes = 0;
        for (Schedule schedule : schedulesForDate) {
            // 确保日程在工作时间内(8:00-18:00)
            LocalDateTime scheduleStartTime = schedule.getStartTime().isBefore(dayStartTime) ? 
                    dayStartTime : schedule.getStartTime();
            LocalDateTime scheduleEndTime = schedule.getEndTime().isAfter(dayEndTime) ? 
                    dayEndTime : schedule.getEndTime();
            
            // 计算日程持续时间(分钟)
            if (scheduleStartTime.isBefore(scheduleEndTime)) {
                long durationMinutes = Duration.between(scheduleStartTime, scheduleEndTime).toMinutes();
                totalScheduledMinutes += durationMinutes;
            }
        }
        
        // 计算剩余可用时间(分钟)
        int remainingMinutes = totalAvailableMinutes - totalScheduledMinutes;
        
        // 确保不会返回负数
        return Math.max(0, remainingMinutes);
    }
    
    /**
     * 获取指定用户在指定日期的剩余可安排时间(分钟)
     * @param date 指定日期
     * @param userId 用户ID
     * @return 用户在该日期的剩余可安排分钟数
     */
    @Override
    public int getRemainingTimeForUserAndDate(LocalDate date, Long userId) {
        // 工作日的开始时间(8:00)和结束时间(18:00)
        LocalDateTime dayStartTime = date.atTime(8, 0);
        LocalDateTime dayEndTime = date.atTime(18, 0);
        
        // 总可用时间(分钟) = 10小时 = 600分钟
        int totalAvailableMinutes = 600;
        
        // 查询该日期该用户已安排的所有日程
        List<Schedule> schedulesForDate = scheduleService.list(new QueryWrapper<Schedule>()
                .eq("DATE(start_time)", date.toString())
                .eq("user_id", userId)); // 只统计特定用户的日程
        
        // 累计已安排的时间(分钟)
        int totalScheduledMinutes = 0;
        for (Schedule schedule : schedulesForDate) {
            // 确保日程在工作时间内(8:00-18:00)
            LocalDateTime scheduleStartTime = schedule.getStartTime().isBefore(dayStartTime) ? 
                    dayStartTime : schedule.getStartTime();
            LocalDateTime scheduleEndTime = schedule.getEndTime().isAfter(dayEndTime) ? 
                    dayEndTime : schedule.getEndTime();
            
            // 计算日程持续时间(分钟)
            if (scheduleStartTime.isBefore(scheduleEndTime)) {
                long durationMinutes = Duration.between(scheduleStartTime, scheduleEndTime).toMinutes();
                totalScheduledMinutes += durationMinutes;
            }
        }
        
        // 计算剩余可用时间(分钟)
        int remainingMinutes = totalAvailableMinutes - totalScheduledMinutes;
        
        // 确保不会返回负数
        return Math.max(0, remainingMinutes);
    }

    @Override
    public List<TaskVo> checkTimeConflict(Task task) {
        List<TaskVo> conflicts = new ArrayList<>();
        
        // 如果没有开始时间或时间花费，无法检查冲突
        if (task.getStartTime() == null || task.getTimeSpend() == null) {
            return conflicts;
        }

        // 计算任务的结束时间
        LocalDateTime taskStartTime = task.getStartTime().atTime(8, 0); // 从8点开始
        LocalDateTime taskEndTime = taskStartTime.plusMinutes(task.getTimeSpend());

        // 如果结束时间超过18:00，直接返回一个特殊的TaskVo表示超时
        if (taskEndTime.isAfter(task.getStartTime().atTime(18, 0))) {
            TaskVo timeOverVo = new TaskVo();
            timeOverVo.setTitle("工作时间超出范围");
            timeOverVo.setDescription("任务结束时间将超过18:00");
            timeOverVo.setScheduleStartTime(taskStartTime);
            timeOverVo.setScheduleEndTime(taskEndTime);
            conflicts.add(timeOverVo);
            return conflicts;
        }

        // 解析任务关联的用户
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
        
        // 检查每个用户的剩余时间
        for (Long userId : userIds) {
            // 获取用户在该日期的剩余时间
            int remainingTime = getRemainingTimeForUserAndDate(task.getStartTime(), userId);
            
            // 如果用户剩余时间不足
            if (remainingTime < task.getTimeSpend()) {
                UmsAdmin user = userService.getById(userId);
                if (user != null) {
                    TaskVo timeConflictVo = new TaskVo();
                    timeConflictVo.setTitle("用户时间不足");
                    timeConflictVo.setDescription(user.getNickName() + " 当天剩余时间不足（剩余" + remainingTime + "分钟，需要" + task.getTimeSpend() + "分钟）");
                    timeConflictVo.setScheduleStartTime(taskStartTime);
                    timeConflictVo.setScheduleEndTime(taskEndTime);
                    
                    List<UmsAdmin> userList = new ArrayList<>();
                    userList.add(user);
                    timeConflictVo.setUserList(userList);
                    
                    conflicts.add(timeConflictVo);
                }
            }
        }
        
        // 检查每个用户在该时间段是否有其他任务
        for (Long userId : userIds) {
            // 获取用户在该日期的所有日程
            List<Schedule> userSchedules = scheduleService.list(new QueryWrapper<Schedule>()
                .eq("user_id", userId)
                .eq("DATE(start_time)", task.getStartTime())
                .orderBy(true, true, "start_time"));

            // 检查是否与现有日程冲突
            for (Schedule schedule : userSchedules) {
                if (schedule.getStartTime() == null || schedule.getEndTime() == null) continue;

                // 检查时间重叠
                if (!(taskEndTime.isBefore(schedule.getStartTime()) || taskStartTime.isAfter(schedule.getEndTime()))) {
                    // 获取冲突任务的详细信息
                    Task conflictTask = this.getById(schedule.getTaskId());
                    if (conflictTask != null) {
                        TaskVo conflictVo = new TaskVo();
                        BeanUtils.copyProperties(conflictTask, conflictVo);
                        conflictVo.setScheduleStartTime(schedule.getStartTime());
                        conflictVo.setScheduleEndTime(schedule.getEndTime());
                        
                        // 获取用户信息
                        UmsAdmin user = userService.getById(userId);
                        if (user != null) {
                            List<UmsAdmin> userList = new ArrayList<>();
                            userList.add(user);
                            conflictVo.setUserList(userList);
                        }
                        
                        conflicts.add(conflictVo);
                    }
                }
            }
        }
        
        return conflicts;
    }
}
