-- 修改schedule表，添加user_id和parent_id字段
ALTER TABLE `schedule` 
ADD COLUMN `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID，标识该日程分配给哪个用户' AFTER `end_time`,
ADD COLUMN `parent_id` bigint NULL DEFAULT NULL COMMENT '父日程ID，用于关联主日程和用户日程' AFTER `user_id`;

-- 为已存在的日程创建用户对应的日程记录
-- 注意：此脚本需要在修改完ScheduleServiceImpl.java后执行
-- 下面的脚本将为每个task.user_data中的用户创建对应的schedule记录

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
            
            -- 遍历用户ID数组（注意：MySQL 8.0+ 才支持JSON_TABLE）
            -- 这里使用简化的方法，假设最多有10个用户
            -- 在实际生产环境中，应该使用更健壮的方法
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

-- 清理可能的重复日程 - 对于同一个任务和用户，如果存在多条记录，保留最近创建的那条
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

-- 更新查询以使用user_id
-- 注意：此处不做修改，因为我们已经修改了Java中的方法实现 