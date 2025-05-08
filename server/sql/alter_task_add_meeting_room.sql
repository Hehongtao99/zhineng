-- 在task表中添加need_meeting_room列
ALTER TABLE `task` ADD COLUMN `need_meeting_room` tinyint(1) DEFAULT 0 COMMENT '是否需要会议室（1需要、0不需要）' AFTER `deadline`;

-- 更新现有的会议安排类任务，将其need_meeting_room设置为1（需要会议室）
UPDATE `task` SET `need_meeting_room` = 1 WHERE `category_id` = 1; 