-- 修改Task表，添加任务提醒时间字段
ALTER TABLE task 
ADD COLUMN reminder_time datetime COMMENT '任务提醒时间';

-- 修改Schedule表，添加结束时间字段
ALTER TABLE schedule 
ADD COLUMN end_time datetime COMMENT '结束时间'; 