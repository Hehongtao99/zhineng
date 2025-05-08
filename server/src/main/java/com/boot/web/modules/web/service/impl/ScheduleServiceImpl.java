package com.boot.web.modules.web.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.ums.model.UmsAdmin;
import com.boot.web.modules.ums.service.UmsAdminService;
import com.boot.web.modules.web.model.Notification;
import com.boot.web.modules.web.model.Resources;
import com.boot.web.modules.web.model.Schedule;
import com.boot.web.modules.web.mapper.ScheduleMapper;
import com.boot.web.modules.web.model.Task;
import com.boot.web.modules.web.service.NotificationService;
import com.boot.web.modules.web.service.ResourcesService;
import com.boot.web.modules.web.service.ScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.web.modules.web.service.TaskService;
import com.boot.web.modules.web.vo.ScheduleVo;
import com.boot.web.modules.web.vo.TaskVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;

/**
 * <p>
 * 日程安排表 服务实现类
 * </p>
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UmsAdminService userService;

    @Autowired
    private ResourcesService resourcesService;

    @Override
    public Page<ScheduleVo> search(Integer pageSize, Integer pageNum, String searchKey, Integer status) {
        // 注意：该方法通过searchSchedule查询，在SQL中已加入parent_id IS NULL条件，确保只显示父日程
        Page<ScheduleVo> page = new Page<>(pageNum, pageSize);
        Page<ScheduleVo> pageResult = scheduleMapper.searchSchedule(page, searchKey, status);
        List<ScheduleVo> scheduleVos =  pageResult.getRecords();

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 遍历每个任务，检测截止时间和冲突
        for (ScheduleVo currentSchedule : scheduleVos) {
            // 检查日程结束时间是否已过期
            if (currentSchedule.getEndTime() != null &&
                currentSchedule.getEndTime().isBefore(now) &&
                (currentSchedule.getStatus() == null || currentSchedule.getStatus() != 4)) {
                // 如果结束时间已过期且不是已完成状态，更新为已逾期状态
                Task task = taskService.getById(currentSchedule.getTaskId());
                if (task != null) {
                    task.setStatus(5); // 修改任务状态为已逾期
                    taskService.updateById(task);
                }
                currentSchedule.setStatus(5); // 同步修改当前日程视图对象的状态
            }

            // 设置无冲突 - 对所有日程都进行冲突检测，不管其状态如何
            currentSchedule.setHasConflict(false);
            currentSchedule.setConflictScheduleIds("");
            
            // 检测日程冲突
            checkForConflicts(scheduleVos, currentSchedule);
        }
        pageResult.setRecords(scheduleVos);

        return pageResult;
    }
    
    /**
     * 检测日程冲突
     * 当一个日程的开始时间早于另一个日程的结束时间时，标记为冲突
     * 或者当日程的结束时间晚于任务的截止时间时，标记为冲突
     */
    private void checkForConflicts(List<ScheduleVo> schedules, ScheduleVo currentSchedule) {
        if (currentSchedule.getStartTime() == null || currentSchedule.getEndTime() == null) {
            return;
        }
        
        // 如果当前日程状态是已完成(4)或已逾期(5)，不进行冲突检测
        if (currentSchedule.getStatus() != null && (currentSchedule.getStatus() == 4 || currentSchedule.getStatus() == 5)) {
            return;
        }
        
            List<Long> conflictIds = new ArrayList<>();
        
        // 1. 检查与其他日程的时间冲突
        for (ScheduleVo otherSchedule : schedules) {
            // 跳过自身和没有开始/结束时间的日程
            if (Objects.equals(currentSchedule.getId(), otherSchedule.getId()) || 
                otherSchedule.getStartTime() == null || 
                otherSchedule.getEndTime() == null) {
                continue;
            }
            
            // 冲突检测: 当前日程的开始时间早于其他日程的结束时间，且当前日程的结束时间晚于其他日程的开始时间
            if (currentSchedule.getStartTime().isBefore(otherSchedule.getEndTime()) && 
                currentSchedule.getEndTime().isAfter(otherSchedule.getStartTime())) {
                
                // 标记冲突
                currentSchedule.setHasConflict(true);
                conflictIds.add(otherSchedule.getId());
            }
        }
        
        // 2. 检查与任务截止时间的冲突
        if (currentSchedule.getTaskId() != null && currentSchedule.getEndTime() != null) {
            // 获取关联任务的截止时间
            Task task = taskService.getById(currentSchedule.getTaskId());
            if (task != null && task.getDeadline() != null) {
                // 如果日程结束时间晚于任务截止时间，标记为冲突
                // 不论任务状态如何，只要有截止时间冲突，就标记为冲突
                if (currentSchedule.getEndTime().isAfter(task.getDeadline())) {
                    currentSchedule.setHasConflict(true);
                    
                    // 将任务ID也添加到冲突列表，以便前端能够显示冲突详情
                    // 这里使用负数任务ID以区分是截止时间冲突而非日程冲突
                    conflictIds.add(-task.getId());
                }
            }
        }
        
        // 如果有冲突，设置冲突的日程ID
        if (!conflictIds.isEmpty()) {
            currentSchedule.setConflictScheduleIds(String.join(",", conflictIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList())));
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
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }

    /**
     * 检查日程是否与现有日程冲突
     */
    private List<Schedule> checkScheduleConflicts(Schedule schedule) {
        if (schedule.getStartTime() == null || schedule.getEndTime() == null || schedule.getUserId() == null) {
            return new ArrayList<>();
        }

        // 获取同一天的所有日程
        LocalDate scheduleDate = schedule.getStartTime().toLocalDate();
        List<Schedule> existingSchedules = this.list(new QueryWrapper<Schedule>()
            .lambda()
            .ne(schedule.getId() != null, Schedule::getId, schedule.getId()) // 如果是更新操作，排除当前日程
            .eq(Schedule::getUserId, schedule.getUserId()) // 只检查同一用户的日程
            .apply("DATE(start_time) = {0}", scheduleDate));

        // 检查冲突
        return existingSchedules.stream()
            .filter(existing -> isTimeOverlap(
                schedule.getStartTime(), schedule.getEndTime(),
                existing.getStartTime(), existing.getEndTime()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(Schedule schedule) {
        Task task = taskService.getById(schedule.getTaskId());
        if (task == null) {
            log.error("无法创建日程，任务ID {} 不存在", schedule.getTaskId());
            return false;
        }
        
        // 设置结束时间，如果未设置则自动根据任务耗时计算
        if (schedule.getEndTime() == null && task.getTimeSpend() != null) {
            // 根据任务预估时间设置结束时间
            schedule.setEndTime(schedule.getStartTime().plusMinutes(task.getTimeSpend()));
        } else if (schedule.getEndTime() == null) {
            // 默认设置结束时间为开始时间后1小时
            schedule.setEndTime(schedule.getStartTime().plusHours(1));
        }

        // 解析任务关联的用户ID
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
        
        boolean success = true;
        
        // 创建管理员日程（主日程）
        Schedule adminSchedule = new Schedule();
        adminSchedule.setTaskId(schedule.getTaskId());
        adminSchedule.setStartTime(schedule.getStartTime());
        adminSchedule.setEndTime(schedule.getEndTime());
        adminSchedule.setUserId(1L); // 设置为管理员ID

        // 检查管理员日程是否有冲突
        List<Schedule> adminConflicts = checkScheduleConflicts(adminSchedule);
        if (!adminConflicts.isEmpty()) {
            log.error("管理员日程存在时间冲突，无法创建");
            return false;
        }
        
        // 保存管理员日程
        success = this.save(adminSchedule);
        
        if (!success) {
            return false;
        }
        
        List<Notification> notifications = new ArrayList<>();
        
        // 为每个参与用户创建一个日程
        for (Long userId : userIds) {
            // 跳过管理员（已经创建过管理员日程）
            if (userId == 1L) continue;
            
            // 创建用户日程
            Schedule userSchedule = new Schedule();
            userSchedule.setTaskId(schedule.getTaskId());
            userSchedule.setStartTime(schedule.getStartTime());
            userSchedule.setEndTime(schedule.getEndTime());
            userSchedule.setUserId(userId);

            // 检查用户日程是否有冲突
            List<Schedule> userConflicts = checkScheduleConflicts(userSchedule);
            if (!userConflicts.isEmpty()) {
                log.error("用户 {} 的日程存在时间冲突，跳过创建", userId);
                continue;
            }
            
            // 保存用户日程
            success = this.save(userSchedule);
            
            if (!success) {
                log.error("为用户 {} 创建日程失败", userId);
                continue;
            }
            
            // 创建通知
            Notification notification = new Notification();
            notification.setScheduleId(userSchedule.getId());
            notification.setUserId(userId);
            notification.setMessage("您有新的任务安排，请及时查看");
            notifications.add(notification);
        }
        
        // 批量保存通知
        if (!notifications.isEmpty()) {
            notificationService.saveBatch(notifications);
        }

        return true;
    }

    @Override
    public boolean updateSchedule(Schedule scheduleUpdates) {
        // 1. Fetch the existing schedule using the ID from scheduleUpdates
        Schedule existingSchedule = this.getById(scheduleUpdates.getId());
        if (existingSchedule == null) {
            log.error("试图更新不存在的日程: ID {}", scheduleUpdates.getId());
            return false;
        }

        // 2. Fetch the associated task using the taskId from the *existing* schedule
        Task task = null;
        if (existingSchedule.getTaskId() != null) {
            task = taskService.getById(existingSchedule.getTaskId());
        }

        if (task == null && existingSchedule.getTaskId() != null) {
            log.warn("日程 {} 关联的任务 {} 未找到", existingSchedule.getId(), existingSchedule.getTaskId());
        }

        // 3. Update startTime and endTime from the scheduleUpdates object
        existingSchedule.setStartTime(scheduleUpdates.getStartTime());
        existingSchedule.setEndTime(scheduleUpdates.getEndTime());

        // 4. Recalculate endTime only if it wasn't provided AND task exists and has timeSpend
        if (existingSchedule.getEndTime() == null && task != null && task.getTimeSpend() != null) {
            existingSchedule.setEndTime(existingSchedule.getStartTime().plusMinutes(task.getTimeSpend()));
        } else if (existingSchedule.getEndTime() == null) {
            existingSchedule.setEndTime(existingSchedule.getStartTime().plusHours(1));
        }

        // 5. Check for conflicts before updating
        List<Schedule> conflicts = checkScheduleConflicts(existingSchedule);
        if (!conflicts.isEmpty()) {
            log.error("日程更新失败：与现有日程时间冲突");
            return false;
        }

        // 6. Save the updated existingSchedule
        boolean success = this.updateById(existingSchedule);

        // 7. Send notification only if update was successful AND task exists
        if (success && task != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
            if (!userIds.isEmpty()) {
                final Task finalTask = task;
                List<Notification> notifications = new ArrayList<>();
                userIds.forEach(id -> {
                    Notification notification = new Notification();
                    notification.setScheduleId(existingSchedule.getId());
                    notification.setUserId(id);
                    notification.setType(2);
                    notification.setMessage("您参与的任务 \"" + finalTask.getTitle() + "\" 执行时间已更新");
                    notifications.add(notification);
                });
                notificationService.saveBatch(notifications);
            }
        } else if (success && task == null) {
            log.warn("日程 {} 更新成功，但无法发送通知，因为关联任务不存在", existingSchedule.getId());
        }

        return success;
    }

    /**
     * 每隔5分钟检查一次，发送任务提醒
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkAndSendReminders() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 获取所有任务
        List<Task> tasks = taskService.list();

        for (Task task : tasks) {
            // 如果设置了提醒时间，并且提醒时间已到或即将到来（5分钟内）
            if (task.getReminderTime() != null &&
                ChronoUnit.MINUTES.between(now, task.getReminderTime()) <= 5 &&
                ChronoUnit.MINUTES.between(now, task.getReminderTime()) >= 0) {

                // 查找此任务关联的日程
                List<Schedule> schedules = this.list(new QueryWrapper<Schedule>().eq("task_id", task.getId()));

                // 如果有关联的日程，发送提醒
                if (!schedules.isEmpty()) {
                    Schedule schedule = schedules.get(0);

                    // 发送提醒
                    ObjectMapper objectMapper = new ObjectMapper();
                    Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
                    
                    // 只有用户ID集合非空时才创建通知
                    if (!userIds.isEmpty()) {
                        List<Notification> notifications = new ArrayList<>();
                        userIds.forEach(id -> {
                            Notification notification = new Notification();
                            notification.setScheduleId(schedule.getId());
                            notification.setUserId(id);
                            notification.setType(3);
                            notification.setMessage("您有任务即将开始，请做好准备");
                            notifications.add(notification);
                        });
                        
                        // 只有有通知时才批量保存
                        if (!notifications.isEmpty()) {
                            notificationService.saveBatch(notifications);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<TaskVo> conflictList(Long id, List<Long> scheduleIds) {
        List<TaskVo> conflictTasks = new ArrayList<>();
        
        // 获取当前日程
        Schedule currentSchedule = this.getById(id);
        if (currentSchedule == null || currentSchedule.getStartTime() == null || currentSchedule.getEndTime() == null) {
            return conflictTasks;
        }
        
        // 如果当前日程关联的任务状态为已完成(4)或已逾期(5)，不检测冲突
        if (currentSchedule.getTaskId() != null) {
            Task currentTask = taskService.getById(currentSchedule.getTaskId());
            if (currentTask != null && currentTask.getStatus() != null && 
                (currentTask.getStatus() == 4 || currentTask.getStatus() == 5)) {
                return conflictTasks;
            }
        }
        
        // 先检查是否有截止时间冲突
        boolean hasDeadlineConflict = false;
        if (currentSchedule.getTaskId() != null) {
            Task currentTask = taskService.getById(currentSchedule.getTaskId());
            if (currentTask != null && currentTask.getDeadline() != null) {
                // 如果任务状态为已完成(4)或已逾期(5)，不检测截止时间冲突
                if (currentTask.getStatus() != 4 && currentTask.getStatus() != 5) {
                    if (currentSchedule.getEndTime().isAfter(currentTask.getDeadline())) {
                        hasDeadlineConflict = true;
                        
                        // 添加本任务到冲突列表，标记为截止时间冲突
                        TaskVo taskVo = new TaskVo();
                        BeanUtils.copyProperties(currentTask, taskVo);
                        taskVo.setScheduleStartTime(currentSchedule.getStartTime());
                        taskVo.setScheduleEndTime(currentSchedule.getEndTime());
                        taskVo.setConflictType("deadline"); // 设置冲突类型为截止时间冲突
                        conflictTasks.add(taskVo);
                    }
                }
            }
        }
        
        // 然后检查与其他日程的冲突
        if (scheduleIds != null && !scheduleIds.isEmpty()) {
            // 过滤负数ID，负数ID表示截止时间冲突，已在上面处理
            List<Long> validScheduleIds = scheduleIds.stream()
                .filter(sid -> sid > 0)
                .collect(Collectors.toList());
            
            if (!validScheduleIds.isEmpty()) {
                List<Schedule> schedulesToCheck = this.listByIds(validScheduleIds);
                
                // 检查每个日程是否有冲突
                for (Schedule schedule : schedulesToCheck) {
                    if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                        continue;
                    }
                    
                    // 获取关联的任务来检查状态
                    Task scheduleTask = taskService.getById(schedule.getTaskId());
                    if (scheduleTask != null && scheduleTask.getStatus() != null && 
                        (scheduleTask.getStatus() == 4 || scheduleTask.getStatus() == 5)) {
                        // 如果任务状态为已完成(4)或已逾期(5)，跳过冲突检测
                        continue;
                    }
                    
                    // 检查时间重叠
                    boolean hasConflict = isTimeOverlap(
                        currentSchedule.getStartTime(), currentSchedule.getEndTime(),
                        schedule.getStartTime(), schedule.getEndTime()
                    );
                    
                    if (hasConflict) {
                        // 获取冲突任务的详细信息
                        Task conflictTask = taskService.getById(schedule.getTaskId());
                        if (conflictTask != null) {
                            // 如果任务状态为已完成(4)或已逾期(5)，跳过冲突检测
                            if (conflictTask.getStatus() != null && (conflictTask.getStatus() == 4 || conflictTask.getStatus() == 5)) {
                                continue;
                            }
                            
                            TaskVo taskVo = new TaskVo();
                            BeanUtils.copyProperties(conflictTask, taskVo);
                            taskVo.setScheduleStartTime(schedule.getStartTime());
                            taskVo.setScheduleEndTime(schedule.getEndTime());
                            taskVo.setConflictType("schedule"); // 设置冲突类型为日程时间冲突
                            conflictTasks.add(taskVo);
                        }
                    }
                }
            }
        } else {
            // 如果没有指定scheduleIds，且没有截止时间冲突，则检查同一天的所有日程
            LocalDate scheduleDate = currentSchedule.getStartTime().toLocalDate();
            List<Schedule> schedulesToCheck = this.list(new QueryWrapper<Schedule>()
                .lambda()
                .ne(Schedule::getId, id) // 排除当前日程
                .eq(Schedule::getUserId, currentSchedule.getUserId()) // 只检查同一用户的日程
                .apply("DATE(start_time) = {0}", scheduleDate));
                
            // 检查每个日程是否有冲突
            for (Schedule schedule : schedulesToCheck) {
                if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                    continue;
                }
                
                // 获取关联的任务来检查状态
                Task scheduleTask = taskService.getById(schedule.getTaskId());
                if (scheduleTask != null && scheduleTask.getStatus() != null && 
                    (scheduleTask.getStatus() == 4 || scheduleTask.getStatus() == 5)) {
                    // 如果任务状态为已完成(4)或已逾期(5)，跳过冲突检测
                    continue;
                }
                
                // 检查时间重叠
                boolean hasConflict = isTimeOverlap(
                    currentSchedule.getStartTime(), currentSchedule.getEndTime(),
                    schedule.getStartTime(), schedule.getEndTime()
                );
                
                if (hasConflict) {
                    // 获取冲突任务的详细信息
                    Task conflictTask = taskService.getById(schedule.getTaskId());
                    if (conflictTask != null) {
                        // 如果任务状态为已完成(4)或已逾期(5)，跳过冲突检测
                        if (conflictTask.getStatus() != null && (conflictTask.getStatus() == 4 || conflictTask.getStatus() == 5)) {
                            continue;
                        }
                        
                        TaskVo taskVo = new TaskVo();
                        BeanUtils.copyProperties(conflictTask, taskVo);
                        taskVo.setScheduleStartTime(schedule.getStartTime());
                        taskVo.setScheduleEndTime(schedule.getEndTime());
                        taskVo.setConflictType("schedule"); // 设置冲突类型为日程时间冲突
                        conflictTasks.add(taskVo);
                    }
                }
            }
        }
        
        return conflictTasks;
    }

    @Override
    public Page<ScheduleVo> searchUser(Integer pageSize, Integer pageNum, String searchKey, Long userId, Integer status) {
        // 注意：该方法通过searchUserSchedule查询，在SQL中已加入parent_id IS NOT NULL条件，确保用户只看到子日程
        // 创建分页参数
        Page<ScheduleVo> page = new Page<>(pageNum, pageSize);
        
        // 使用专门的用户日程查询
        Page<ScheduleVo> userSchedulePage = scheduleMapper.searchUserSchedule(page, userId, searchKey, status);
        
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        
        // 处理每个日程的状态和冲突检测
        List<ScheduleVo> scheduleVos = userSchedulePage.getRecords();
        
        // 遍历每个任务，检测截止时间和冲突
        for (ScheduleVo currentSchedule : scheduleVos) {
            // 检查日程结束时间是否已过期
            if (currentSchedule.getEndTime() != null &&
                currentSchedule.getEndTime().isBefore(now) &&
                (currentSchedule.getStatus() == null || currentSchedule.getStatus() != 4)) {
                // 如果结束时间已过期且不是已完成状态，更新为已逾期状态
                Task task = taskService.getById(currentSchedule.getTaskId());
                if (task != null) {
                    task.setStatus(5); // 修改任务状态为已逾期
                    taskService.updateById(task);
                }
                currentSchedule.setStatus(5); // 同步修改当前日程视图对象的状态
            }
            
            // 设置无冲突 - 对所有日程都进行冲突检测，不管其状态如何
            currentSchedule.setHasConflict(false);
            currentSchedule.setConflictScheduleIds("");
            
            // 检测日程冲突
            checkForConflicts(scheduleVos, currentSchedule);
        }
        
        userSchedulePage.setRecords(scheduleVos);
        
        return userSchedulePage;
    }

    @Override
    public Map<String, List<ScheduleVo>> getSchedulesByMonth(String yearMonth) {
        // 管理员可以查看所有主日程，但应该按用户ID分组避免重复
        List<ScheduleVo> schedules = scheduleMapper.findSchedulesByMonth(yearMonth);
        Map<String, List<ScheduleVo>> result = new HashMap<>();

        // 按日期分组
        for (ScheduleVo schedule : schedules) {
            if (schedule.getStartTime() != null) {
                String date = schedule.getStartTime().toLocalDate().toString();
                if (!result.containsKey(date)) {
                    result.put(date, new ArrayList<>());
                }
                result.get(date).add(schedule);
            }
        }

        return result;
    }

    @Override
    public Map<String, List<ScheduleVo>> getSchedulesByMonthAndUser(String yearMonth, Long userId, boolean isAdmin) {
        List<ScheduleVo> schedules;

        if (isAdmin) {
            // 管理员可以查看所有日程
            schedules = scheduleMapper.findSchedulesByMonth(yearMonth);
        } else {
            // 普通用户只能查看自己相关的日程
            schedules = scheduleMapper.findSchedulesByMonthAndUser(yearMonth, userId);
        }

        Map<String, List<ScheduleVo>> result = new HashMap<>();

        // 按日期分组
        for (ScheduleVo schedule : schedules) {
            if (schedule.getStartTime() != null) {
                String date = schedule.getStartTime().toLocalDate().toString();
                if (!result.containsKey(date)) {
                    result.put(date, new ArrayList<>());
                }
                result.get(date).add(schedule);
            }
        }

        return result;
    }

    /**
     * 计算日程的优先级得分
     * 优先级计算基于任务截止时间、任务创建时间和任务状态等因素
     * 
     * @param schedule 日程对象
     * @param task 相关联的任务对象
     * @return 优先级得分，分数越高优先级越高
     */
    private double calculatePriorityScore(ScheduleVo schedule, Task task) {
        double priorityScore = 0.0;
        
        // 如果没有关联任务，则优先级较低
        if (task == null) {
            return 0.0;
        }
        
        // 根据截止时间计算优先级（距离截止时间越近，得分越高）
        if (task.getDeadline() != null) {
            LocalDateTime now = LocalDateTime.now();
            long hoursToDeadline = ChronoUnit.HOURS.between(now, task.getDeadline());
            
            // 截止时间越近，得分越高（最高100分）
            if (hoursToDeadline <= 0) {
                // 已经过期，优先级最高
                priorityScore += 100;
            } else if (hoursToDeadline <= 24) {
                // 24小时内到期，高优先级
                priorityScore += 90;
            } else if (hoursToDeadline <= 72) {
                // 3天内到期，较高优先级
                priorityScore += 80;
            } else if (hoursToDeadline <= 168) {
                // 一周内到期，中等优先级
                priorityScore += 70;
            } else {
                // 超过一周，较低优先级
                priorityScore += 50;
            }
        }
        
        // 根据任务状态调整优先级
        if (task.getStatus() != null) {
            switch (task.getStatus()) {
                case 0: // 待办
                    priorityScore += 20;
                    break;
                case 1: // 进行中
                    priorityScore += 30;
                    break;
                case 2: // 已完成
                    priorityScore -= 50; // 已完成任务优先级降低
                    break;
                default:
                    // 其他状态不调整
                    break;
            }
        }
        
        // 考虑创建时间（较早创建的任务优先级略高）
        if (task.getCreateTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            // 将Date转换为LocalDateTime
            LocalDateTime createTime = task.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            long daysSinceCreation = ChronoUnit.DAYS.between(createTime, now);
            
            // 任务创建时间越久，优先级略微提高（最高加10分）
            priorityScore += Math.min(daysSinceCreation / 2, 10);
        }
        
        return priorityScore;
    }

    @Override
    public Page<ScheduleVo> smartSort(Integer pageSize, Integer pageNum, String searchKey) {
        // 获取原始日程列表（已包含冲突检测）
        Page<ScheduleVo> originalPage = search(pageSize, pageNum, searchKey, null);
        List<ScheduleVo> scheduleList = originalPage.getRecords();

        // 计算每个日程的优先级得分
        Map<Long, Double> schedulePriorityScores = new HashMap<>();

        for (ScheduleVo schedule : scheduleList) {
            Task task = null;
            if (schedule.getTaskId() != null) {
                task = taskService.getById(schedule.getTaskId());
            }

            // 计算优先级得分
            double priorityScore = calculatePriorityScore(schedule, task);
            schedulePriorityScores.put(schedule.getId(), priorityScore);
            
            // 设置到日程对象中，用于前端显示
            schedule.setPriorityScore(priorityScore);
        }

        // 根据优先级得分排序日程
        scheduleList.sort((a, b) -> {
            Double scoreA = schedulePriorityScores.getOrDefault(a.getId(), 0.0);
            Double scoreB = schedulePriorityScores.getOrDefault(b.getId(), 0.0);

            // 分数高的排在前面
            return Double.compare(scoreB, scoreA);
        });

        // 返回排序后的结果
        originalPage.setRecords(scheduleList);
        return originalPage;
    }
    
    @Override
    public Page<ScheduleVo> smartSortUser(Integer pageSize, Integer pageNum, String searchKey, Long userId) {
        // 获取原始日程列表（已包含冲突检测）
        Page<ScheduleVo> originalPage = searchUser(pageSize, pageNum, searchKey, userId, null);
        List<ScheduleVo> scheduleList = originalPage.getRecords();

        // 计算每个日程的优先级得分
        Map<Long, Double> schedulePriorityScores = new HashMap<>();

        for (ScheduleVo schedule : scheduleList) {
            Task task = null;
            if (schedule.getTaskId() != null) {
                task = taskService.getById(schedule.getTaskId());
            }

            // 计算优先级得分
            double priorityScore = calculatePriorityScore(schedule, task);
            schedulePriorityScores.put(schedule.getId(), priorityScore);
            
            // 设置到日程对象中，用于前端显示
            schedule.setPriorityScore(priorityScore);
        }

        // 根据优先级得分排序日程
        scheduleList.sort((a, b) -> {
            Double scoreA = schedulePriorityScores.getOrDefault(a.getId(), 0.0);
            Double scoreB = schedulePriorityScores.getOrDefault(b.getId(), 0.0);

            // 分数高的排在前面
            return Double.compare(scoreB, scoreA);
        });

        // 返回排序后的结果
        originalPage.setRecords(scheduleList);
        return originalPage;
    }

    /**
     * 每小时检查一次任务截止时间，更新逾期任务状态
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void checkAndUpdateOverdueTasks() {
        log.info("执行任务逾期状态检查...");
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 查询所有待办状态的任务
        List<Task> tasks = taskService.list(new QueryWrapper<Task>().lambda()
                .eq(Task::getStatus, 1));

        // 查找每个任务对应的日程，检查日程结束时间是否已过期
        for (Task task : tasks) {
            boolean isOverdue = false;
            
            // 1. 先检查任务是否有关联的日程
            List<Schedule> schedules = this.list(new QueryWrapper<Schedule>().eq("task_id", task.getId()));

            // 如果任务有对应的日程
            if (!schedules.isEmpty()) {
                for (Schedule schedule : schedules) {
                    // 如果日程有结束时间，且结束时间已过，更新任务状态为已逾期
                    if (schedule.getEndTime() != null && schedule.getEndTime().isBefore(now)) {
                        isOverdue = true;
                        break;
                    }
                }
            }
            // 2. 如果没有日程，检查任务的开始时间和预估耗时
            else if (task.getStartTime() != null && task.getTimeSpend() != null) {
                // 计算任务的预期结束时间：开始日期转为当天早上8点时间 + 预估耗时
                LocalDateTime expectedEndTime = task.getStartTime().atTime(8, 0).plusMinutes(task.getTimeSpend());
                // 如果当前时间已超过预期结束时间，则设为已逾期
                if (now.isAfter(expectedEndTime)) {
                    isOverdue = true;
                }
            }
            // 3. 如果没有日程也没有开始时间和预估耗时，则检查截止时间
            else if (task.getDeadline() != null && task.getDeadline().isBefore(now)) {
                isOverdue = true;
            }
            
            // 如果任务确定已逾期，更新状态并发送通知
            if (isOverdue) {
                // 将状态更新为已逾期
                task.setStatus(5);
                taskService.updateById(task);
                
                // 发送逾期通知
                if (!schedules.isEmpty()) {
                    // 使用第一个日程作为通知对象
                    Schedule schedule = schedules.get(0);
                    
                    // 解析用户数据
                    ObjectMapper objectMapper = new ObjectMapper();
                    Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
                    
                    if (!userIds.isEmpty()) {
                        // 发送逾期通知
                        List<Notification> notifications = new ArrayList<>();
                        userIds.forEach(id -> {
                            Notification notification = new Notification();
                            notification.setScheduleId(schedule.getId());
                            notification.setUserId(id);
                            notification.setType(4); // 逾期通知类型
                            notification.setMessage("您的任务 \"" + task.getTitle() + "\" 已逾期，请尽快处理");
                            notifications.add(notification);
                        });
                        notificationService.saveBatch(notifications);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustScheduleOrder(Long scheduleId, LocalDateTime targetTime, LocalDate date) {
        try {
            // 1. 获取被拖动的日程
            Schedule movedSchedule = this.getById(scheduleId);
            if (movedSchedule == null) {
                log.error("未找到ID为 {} 的日程", scheduleId);
                return false;
            }

            // 2. 获取任务信息以确定持续时间和用户ID
            Task task = taskService.getById(movedSchedule.getTaskId());
            if (task == null) {
                log.error("未找到日程 {} 关联的任务", scheduleId);
                return false;
            }

            // 3. 确定当前操作的用户ID
            Long userId;
            
            // 如果日程有设置用户ID，优先使用日程的用户ID
            if (movedSchedule.getUserId() != null) {
                userId = movedSchedule.getUserId();
            } else {
                // 否则从任务数据中获取
                ObjectMapper objectMapper = new ObjectMapper();
                Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
                userId = userIds.stream()
                        .filter(uid -> uid != 1) // 排除管理员
                        .findFirst()
                        .orElse(1L); // 默认为管理员ID
            }

            // 4. 计算日程持续时间（分钟）
            long durationMinutes;
            if (movedSchedule.getEndTime() != null && movedSchedule.getStartTime() != null) {
                durationMinutes = Duration.between(movedSchedule.getStartTime(), movedSchedule.getEndTime()).toMinutes();
            } else if (task.getTimeSpend() != null) {
                durationMinutes = task.getTimeSpend();
            } else {
                durationMinutes = 60; // 默认1小时
            }

            // 5. 确定工作时间范围
            LocalDateTime dayStartTime = date.atTime(8, 0);
            LocalDateTime dayEndTime = date.atTime(18, 0);
            
            // 6. 获取当天该用户的所有日程（除了被拖动的日程）
            List<Schedule> userDaySchedules = this.list(new QueryWrapper<Schedule>()
                    .eq("DATE(start_time)", date.toString())
                    .eq("user_id", userId)
                    .ne("id", scheduleId)  // 排除被拖动的日程
                    .orderBy(true, true, "start_time"));
            
            // 创建需要更新的日程列表
            List<Schedule> schedulesToUpdate = new ArrayList<>();
            
            // 7. 处理特殊情况：如果当天没有其他日程或者是将日程移动到当天的第一个位置
            boolean isFirstTask = userDaySchedules.isEmpty() || targetTime.isBefore(userDaySchedules.get(0).getStartTime());
            
            // 8. 如果是第一个任务或当天没有其他任务
            if (isFirstTask) {
                // 第一个任务固定从8:00开始
                movedSchedule.setStartTime(dayStartTime);
                movedSchedule.setEndTime(dayStartTime.plusMinutes(durationMinutes));
                
                // 检查是否超过工作时间
                if (movedSchedule.getEndTime().isAfter(dayEndTime)) {
                    log.warn("日程结束时间超过当天工作时间限制");
                    return false;
                }
                
                schedulesToUpdate.add(movedSchedule);
                
                // 调整后续所有任务的时间
                LocalDateTime currentTime = movedSchedule.getEndTime();
                
                for (Schedule schedule : userDaySchedules) {
                    // 获取日程持续时间 - 修复：使用保存好的任务信息确保持续时间一致
                    long scheduleDuration;
                    Task scheduleTask = taskService.getById(schedule.getTaskId());
                    if (scheduleTask != null && scheduleTask.getTimeSpend() != null) {
                        scheduleDuration = scheduleTask.getTimeSpend();
                    } else if (schedule.getEndTime() != null && schedule.getStartTime() != null) {
                        scheduleDuration = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();
                    } else {
                        scheduleDuration = 60; // 默认1小时
                    }
                    
                    // 设置新的时间
                    schedule.setStartTime(currentTime);
                    schedule.setEndTime(currentTime.plusMinutes(scheduleDuration));
                    
                    // 检查是否超过工作时间
                    if (schedule.getEndTime().isAfter(dayEndTime)) {
                        log.warn("后续日程将超出工作时间限制，停止调整");
                        return false;
                    }
                    
                    schedulesToUpdate.add(schedule);
                    
                    // 更新下一个日程的开始时间
                    currentTime = schedule.getEndTime();
                }
            } 
            // 9. 如果是将日程插入到某个位置
            else {
                // 找到目标时间对应的插入位置
                int insertIndex = 0;
                while (insertIndex < userDaySchedules.size() && 
                       userDaySchedules.get(insertIndex).getStartTime().isBefore(targetTime)) {
                    insertIndex++;
                }
                
                // 如果前面有日程，新日程的开始时间应该是前一个日程的结束时间
                LocalDateTime currentTime;
                if (insertIndex > 0) {
                    Schedule prevSchedule = userDaySchedules.get(insertIndex - 1);
                    currentTime = prevSchedule.getEndTime();
                } else {
                    // 否则从8点开始
                    currentTime = dayStartTime;
                }
                
                // 重新设置被拖动日程的时间
                movedSchedule.setStartTime(currentTime);
                movedSchedule.setEndTime(currentTime.plusMinutes(durationMinutes));
                
                // 检查是否超过工作时间
                if (movedSchedule.getEndTime().isAfter(dayEndTime)) {
                    log.warn("日程结束时间超过当天工作时间限制");
                    return false;
                }
                
                schedulesToUpdate.add(movedSchedule);
                
                // 更新下一个位置的开始时间
                currentTime = movedSchedule.getEndTime();
                
                // 调整后续所有任务的时间
                for (int i = insertIndex; i < userDaySchedules.size(); i++) {
                    Schedule schedule = userDaySchedules.get(i);
                    
                    // 获取日程持续时间 - 修复：使用保存好的任务信息确保持续时间一致
                    long scheduleDuration;
                    Task scheduleTask = taskService.getById(schedule.getTaskId());
                    if (scheduleTask != null && scheduleTask.getTimeSpend() != null) {
                        scheduleDuration = scheduleTask.getTimeSpend();
                    } else if (schedule.getEndTime() != null && schedule.getStartTime() != null) {
                        scheduleDuration = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();
                    } else {
                        scheduleDuration = 60; // 默认1小时
                    }
                    
                    // 设置新的时间
                    schedule.setStartTime(currentTime);
                    schedule.setEndTime(currentTime.plusMinutes(scheduleDuration));
                    
                    // 检查是否超过工作时间
                    if (schedule.getEndTime().isAfter(dayEndTime)) {
                        log.warn("后续日程将超出工作时间限制，停止调整");
                        return false;
                    }
                    
                    schedulesToUpdate.add(schedule);
                    
                    // 更新下一个日程的开始时间
                    currentTime = schedule.getEndTime();
                }
            }
            
            // 10. 批量更新所有需要调整的日程
            return this.updateBatchById(schedulesToUpdate);
            
        } catch (Exception e) {
            log.error("调整日程顺序时发生错误", e);
            return false;
        }
    }

    @Override
    public Page<ScheduleVo> getAllUsersSchedulesByWeight(Integer pageSize, Integer pageNum, String searchKey, Integer status) {
        // 创建分页参数
        Page<ScheduleVo> page = new Page<>(pageNum, pageSize);
        
        // 获取所有日程
        List<ScheduleVo> allSchedules = scheduleMapper.searchAllUsersSchedules(searchKey, status);
        
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        
        // 遍历每个任务，检测截止时间和冲突
        for (ScheduleVo currentSchedule : allSchedules) {
            // 检查日程结束时间是否已过期
            if (currentSchedule.getEndTime() != null &&
                currentSchedule.getEndTime().isBefore(now) &&
                (currentSchedule.getStatus() == null || currentSchedule.getStatus() != 4)) {
                // 如果结束时间已过期且不是已完成状态，更新为已逾期状态
                Task task = taskService.getById(currentSchedule.getTaskId());
                if (task != null) {
                    task.setStatus(5); // 修改任务状态为已逾期
                    taskService.updateById(task);
                }
                currentSchedule.setStatus(5); // 同步修改当前日程视图对象的状态
            }
            
            // 设置无冲突 - 对所有日程都进行冲突检测，不管其状态如何
            currentSchedule.setHasConflict(false);
            currentSchedule.setConflictScheduleIds("");
            
            // 检测日程冲突
            checkForConflicts(allSchedules, currentSchedule);
        }
        
        // 计算每个日程的优先级得分
        Map<Long, Double> schedulePriorityScores = new HashMap<>();
        
        for (ScheduleVo schedule : allSchedules) {
            Task task = null;
            if (schedule.getTaskId() != null) {
                task = taskService.getById(schedule.getTaskId());
            }
            
            // 计算优先级得分
            double priorityScore = calculatePriorityScore(schedule, task);
            schedulePriorityScores.put(schedule.getId(), priorityScore);
            
            // 设置到日程对象中，用于前端显示
            schedule.setPriorityScore(priorityScore);
            
            // 清除时间信息（只显示顺序，不显示具体时间）
            // 注意：在检测完冲突后才清除时间信息
            schedule.setStartTime(null);
            schedule.setEndTime(null);
        }
        
        // 根据优先级得分排序日程
        allSchedules.sort((a, b) -> {
            Double scoreA = schedulePriorityScores.getOrDefault(a.getId(), 0.0);
            Double scoreB = schedulePriorityScores.getOrDefault(b.getId(), 0.0);
            
            // 分数高的排在前面
            return Double.compare(scoreB, scoreA);
        });
        
        // 手动分页
        int total = allSchedules.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        // 避免索引越界
        if (fromIndex >= total) {
            fromIndex = 0;
            toIndex = 0;
        }
        
        List<ScheduleVo> pageData = fromIndex < toIndex ? allSchedules.subList(fromIndex, toIndex) : new ArrayList<>();
        
        // 设置分页信息
        page.setRecords(pageData);
        page.setTotal(total);
        
        return page;
    }
}
