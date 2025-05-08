# 用户日程显示BUG修复步骤（更新版）

## 问题描述

管理员创建任务分配给其他用户后，其他用户能够显示日历，但是无法显示分配给他们的日程安排。另外，在尝试修复后，管理员的日历视图出现日程重复显示的问题。

## 问题原因

1. 在当前实现中，系统在任务分配时只创建了一条主日程记录，而没有为每个用户分别创建日程记录。
2. SQL查询和Java方法中未能正确处理并区分主日程和用户日程，导致重复显示或无法显示。

## 修复步骤

### 1. 修改数据模型

1. 打开 `server/src/main/java/com/boot/web/modules/web/model/Schedule.java`
2. 添加以下字段:
   - `userId` - 日程关联的用户ID
   - `parentId` - 父日程ID，用于关联主日程和用户日程

### 2. 修改数据查询方法

1. 更新 `ScheduleMapper.xml` 中的所有查询方法，确保它们返回 `userId` 和 `parentId` 字段:
   - `searchSchedule`
   - `findAllSchedules`
   - `findByIds`
   - `findSchedulesByMonth`
   - `findSchedulesByMonthAndUser`

2. 修改 `findSchedulesByMonthAndUser` 查询，使其同时通过 `task.user_data` 和 `schedule.user_id` 查询用户日程。

### 3. 修改业务逻辑

1. 修改 `ScheduleServiceImpl.java` 中的 `create` 方法，为每个任务关联的用户创建单独的日程。
2. 修改 `getSchedulesByMonthAndUser` 方法，确保只返回用户关联的日程且避免重复显示。
3. 修改 `searchUser` 方法，使其能同时通过 `task.user_data` 和 `schedule.user_id` 查询用户日程。

### 4. 数据库表结构更新

1. 创建 `alter_schedule_table.sql` 脚本，包含修改表结构和数据迁移的SQL语句。
2. 在数据库中执行SQL脚本，添加新的列并创建用户日程记录。
3. 添加清理重复数据的逻辑，确保同一用户同一任务只有一条日程记录。

## 详细修改内容

### Schedule模型添加字段

```java
@ApiModelProperty("用户ID，标识该日程分配给哪个用户")
private Long userId;

@ApiModelProperty("父日程ID，用于关联主日程和用户日程")
private Long parentId;
```

### 更新ScheduleServiceImpl的create方法

```java
@Override
@Transactional(rollbackFor = Exception.class)
public Boolean create(Schedule schedule) {
    Task task = taskService.getById(schedule.getTaskId());
    if (task == null) {
        log.error("无法创建日程，任务ID {} 不存在", schedule.getTaskId());
        return false;
    }
    
    // 修改任务状态
    task.setStatus(2);

    // 设置结束时间，如果未设置则自动根据任务耗时计算
    if (schedule.getEndTime() == null && task.getTimeSpend() != null) {
        // 根据任务预估时间设置结束时间
        schedule.setEndTime(schedule.getStartTime().plusMinutes(task.getTimeSpend()));
    } else if (schedule.getEndTime() == null) {
        // 默认设置结束时间为开始时间后1小时
        schedule.setEndTime(schedule.getStartTime().plusHours(1));
    }

    taskService.updateById(task);

    // 解析任务关联的用户ID
    ObjectMapper objectMapper = new ObjectMapper();
    Set<Long> userIds = parseJsonArrayToSet(task.getUserData(), objectMapper);
    
    boolean success = true;
    
    // 如果用户列表不为空，为每个用户创建单独的日程
    if (!userIds.isEmpty()) {
        // 保存原始日程作为主日程
        success = this.save(schedule);
        Long mainScheduleId = schedule.getId();
        
        // 为每个用户创建单独的日程记录
        List<Schedule> userSchedules = new ArrayList<>();
        List<Notification> notifications = new ArrayList<>();
        
        for (Long userId : userIds) {
            // 跳过管理员（假设ID为1的是管理员）
            if (userId == 1) continue;
            
            // 创建用户专属日程
            Schedule userSchedule = new Schedule();
            userSchedule.setTaskId(schedule.getTaskId());
            userSchedule.setStartTime(schedule.getStartTime());
            userSchedule.setEndTime(schedule.getEndTime());
            userSchedule.setUserId(userId); // 设置用户ID
            userSchedule.setParentId(mainScheduleId); // 关联到主日程
            userSchedules.add(userSchedule);
            
            // 创建用户通知
            Notification notification = new Notification();
            notification.setScheduleId(mainScheduleId); // 使用主日程ID
            notification.setUserId(userId);
            notification.setMessage("您有新的任务安排，请及时查看");
            notifications.add(notification);
        }
        
        // 批量保存用户日程和通知
        if (!userSchedules.isEmpty()) {
            success = success && this.saveBatch(userSchedules);
        }
        
        if (!notifications.isEmpty()) {
            notificationService.saveBatch(notifications);
        }
    } else {
        // 没有关联用户，只保存主日程
        success = this.save(schedule);
    }

    return success;
}
```

### 修改getSchedulesByMonthAndUser方法

```java
@Override
public Map<String, List<ScheduleVo>> getSchedulesByMonthAndUser(String yearMonth, Long userId, boolean isAdmin) {
    List<ScheduleVo> schedules;

    if (isAdmin) {
        // 管理员获取所有日程，但要过滤掉子日程（只保留主日程）
        List<ScheduleVo> allSchedules = scheduleMapper.findSchedulesByMonth(yearMonth);
        // 过滤掉不是管理员自己的子日程
        schedules = allSchedules.stream()
            .filter(s -> s.getParentId() == null || s.getUserId() == null || s.getUserId().equals(userId))
            .collect(Collectors.toList());
    } else {
        // 普通用户只获取与自己相关的日程
        schedules = scheduleMapper.findSchedulesByMonthAndUser(yearMonth, userId);
        
        // 处理重复的日程，优先保留user_id等于当前用户的日程
        // 首先按taskId分组
        Map<Long, List<ScheduleVo>> schedulesByTask = schedules.stream()
            .collect(Collectors.groupingBy(ScheduleVo::getTaskId));
        
        // 处理后的日程列表
        List<ScheduleVo> filteredSchedules = new ArrayList<>();
        
        // 对每个任务的日程进行处理
        for (Map.Entry<Long, List<ScheduleVo>> entry : schedulesByTask.entrySet()) {
            List<ScheduleVo> taskSchedules = entry.getValue();
            
            // 查找该用户专属的日程（userId等于当前用户）
            List<ScheduleVo> userSchedules = taskSchedules.stream()
                .filter(s -> s.getUserId() != null && s.getUserId().equals(userId))
                .collect(Collectors.toList());
            
            if (!userSchedules.isEmpty()) {
                // 如果找到用户专属日程，只添加这些日程
                filteredSchedules.addAll(userSchedules);
            } else {
                // 否则添加通过task.user_data找到的日程
                filteredSchedules.addAll(taskSchedules);
            }
        }
        
        schedules = filteredSchedules;
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
```

## 数据库执行脚本

```sql
-- 修改schedule表，添加user_id和parent_id字段
ALTER TABLE `schedule` 
ADD COLUMN `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID，标识该日程分配给哪个用户' AFTER `end_time`,
ADD COLUMN `parent_id` bigint NULL DEFAULT NULL COMMENT '父日程ID，用于关联主日程和用户日程' AFTER `user_id`;

-- 为已存在的日程创建用户对应的日程记录
-- 临时存储过程，批量创建用户日程
DELIMITER //
CREATE PROCEDURE create_user_schedules()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE schedule_id BIGINT;
    DECLARE task_id BIGINT;
    DECLARE start_time DATETIME;
    DECLARE end_time DATETIME;
    DECLARE user_ids JSON;
    
    -- 创建游标查询所有现有日程
    DECLARE cur CURSOR FOR 
        SELECT s.id, s.task_id, s.start_time, s.end_time, t.user_data 
        FROM schedule s 
        JOIN task t ON s.task_id = t.id 
        WHERE s.user_id IS NULL AND s.parent_id IS NULL;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO schedule_id, task_id, start_time, end_time, user_ids;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 查询任务中的用户数据并为每个用户创建日程
        IF user_ids IS NOT NULL AND JSON_LENGTH(user_ids) > 0 THEN
            -- 将主日程标记为管理员的日程
            UPDATE schedule SET user_id = 1 WHERE id = schedule_id;
            
            -- 遍历用户ID数组
            SET @i = 0;
            WHILE @i < JSON_LENGTH(user_ids) DO
                SET @user_id = JSON_EXTRACT(user_ids, CONCAT('$[', @i, ']'));
                SET @user_id_num = REPLACE(REPLACE(@user_id, '"', ''), ' ', '');
                
                -- 检查用户ID不为管理员ID(1)
                IF @user_id_num != '1' THEN
                    -- 插入新的用户日程记录
                    INSERT INTO schedule(task_id, start_time, end_time, user_id, parent_id, create_time)
                    VALUES(task_id, start_time, end_time, @user_id_num, schedule_id, NOW());
                END IF;
                
                SET @i = @i + 1;
            END WHILE;
        END IF;
        
    END LOOP;
    
    CLOSE cur;
END //
DELIMITER ;

-- 执行存储过程
CALL create_user_schedules();

-- 删除临时存储过程
DROP PROCEDURE create_user_schedules;

-- 清理可能的重复日程
CREATE TEMPORARY TABLE temp_duplicates AS
SELECT 
    MAX(id) as keep_id,
    task_id,
    user_id
FROM 
    schedule
WHERE 
    user_id IS NOT NULL
GROUP BY 
    task_id, user_id
HAVING 
    COUNT(*) > 1;

-- 删除重复记录，保留最新的一条
DELETE s FROM schedule s
JOIN temp_duplicates td ON s.task_id = td.task_id AND s.user_id = td.user_id
WHERE s.id != td.keep_id;

-- 移除临时表
DROP TEMPORARY TABLE IF EXISTS temp_duplicates;
```

## 验证方法

1. 重启服务器应用程序
2. 管理员账号登录，创建一个新任务并分配给其他用户
3. 使用其他用户账号登录，检查是否可以在日历中看到分配的任务
4. 检查管理员的日历视图，确认不再有重复的日程显示 