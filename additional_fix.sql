-- 数据修复脚本：检测并修复日程记录
-- 执行此脚本以确保所有任务都为对应用户创建了日程记录

-- 1. 检查是否已有user_id字段，如果没有则先添加字段
SELECT COUNT(*) INTO @column_exists 
FROM information_schema.columns 
WHERE table_schema = DATABASE() 
  AND table_name = 'schedule' 
  AND column_name = 'user_id';

-- 如果user_id字段不存在，则添加字段
SET @alter_stmt = IF(@column_exists = 0, 
                      'ALTER TABLE `schedule` ADD COLUMN `user_id` bigint NULL DEFAULT NULL COMMENT "用户ID，标识该日程分配给哪个用户" AFTER `end_time`', 
                      'SELECT "user_id字段已存在" AS message');
PREPARE stmt FROM @alter_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 检查是否已有parent_id字段，如果没有则先添加字段
SELECT COUNT(*) INTO @column_exists 
FROM information_schema.columns 
WHERE table_schema = DATABASE() 
  AND table_name = 'schedule' 
  AND column_name = 'parent_id';

-- 如果parent_id字段不存在，则添加字段
SET @alter_stmt = IF(@column_exists = 0, 
                      'ALTER TABLE `schedule` ADD COLUMN `parent_id` bigint NULL DEFAULT NULL COMMENT "父日程ID，用于关联主日程和用户日程" AFTER `user_id`', 
                      'SELECT "parent_id字段已存在" AS message');
PREPARE stmt FROM @alter_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 修复数据：为现有的schedule记录补充关联信息
DROP PROCEDURE IF EXISTS fix_user_schedules;

DELIMITER //
CREATE PROCEDURE fix_user_schedules()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE schedule_id BIGINT;
    DECLARE task_id BIGINT;
    DECLARE start_time DATETIME;
    DECLARE end_time DATETIME;
    DECLARE user_ids JSON;
    DECLARE u_id BIGINT;
    DECLARE p_id BIGINT;
    
    -- 初始化错误处理
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
        @sqlstate = RETURNED_SQLSTATE, @errno = MYSQL_ERRNO, @text = MESSAGE_TEXT;
        SELECT CONCAT('ERROR ', @errno, ' (', @sqlstate, '): ', @text) AS `Error`;
        ROLLBACK;
    END;
    
    -- 创建游标查询所有主日程（没有parent_id的日程）
    DECLARE cur CURSOR FOR 
        SELECT s.id, s.task_id, s.start_time, s.end_time, t.user_data, s.user_id, s.parent_id
        FROM schedule s 
        JOIN task t ON s.task_id = t.id 
        WHERE s.parent_id IS NULL;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    START TRANSACTION;
    
    -- 先将没有user_id的主日程设置为管理员(id=1)的日程
    UPDATE schedule s
    SET s.user_id = 1
    WHERE s.user_id IS NULL AND s.parent_id IS NULL;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO schedule_id, task_id, start_time, end_time, user_ids, u_id, p_id;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 查询任务中的用户数据并为每个用户创建日程
        IF user_ids IS NOT NULL AND JSON_LENGTH(user_ids) > 0 THEN
            -- 遍历用户ID数组
            SET @i = 0;
            WHILE @i < JSON_LENGTH(user_ids) DO
                SET @user_id = JSON_EXTRACT(user_ids, CONCAT('$[', @i, ']'));
                SET @user_id_num = REPLACE(REPLACE(@user_id, '"', ''), ' ', '');
                
                -- 检查用户ID不为管理员ID(1)且不等于schedule记录的user_id
                IF @user_id_num != '1' AND @user_id_num != u_id THEN
                    -- 检查该用户是否已有针对该任务的日程记录
                    SELECT COUNT(*) INTO @schedule_exists
                    FROM schedule
                    WHERE task_id = task_id AND user_id = @user_id_num;
                    
                    -- 如果不存在，则创建用户专属日程
                    IF @schedule_exists = 0 THEN
                        INSERT INTO schedule(task_id, start_time, end_time, user_id, parent_id, create_time)
                        VALUES(task_id, start_time, end_time, @user_id_num, schedule_id, NOW());
                    END IF;
                END IF;
                
                SET @i = @i + 1;
            END WHILE;
        END IF;
    END LOOP;
    
    CLOSE cur;
    
    -- 清理可能的重复记录
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_duplicates AS 
    SELECT 
        MIN(id) as keep_id,
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
    
    -- 删除重复的日程，保留ID最小的一条
    DELETE s FROM schedule s
    JOIN temp_duplicates t ON s.task_id = t.task_id AND s.user_id = t.user_id
    WHERE s.id != t.keep_id;
    
    DROP TEMPORARY TABLE IF EXISTS temp_duplicates;
    
    COMMIT;
END //
DELIMITER ;

-- 执行存储过程
CALL fix_user_schedules();

-- 删除临时存储过程
DROP PROCEDURE IF EXISTS fix_user_schedules;

-- 输出修复结果
SELECT 'Data fixed successfully' AS Result; 