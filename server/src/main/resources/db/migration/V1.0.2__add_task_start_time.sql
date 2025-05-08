-- 修改Task表，添加任务开始时间字段
ALTER TABLE task 
ADD COLUMN start_time datetime COMMENT '任务开始时间'; 