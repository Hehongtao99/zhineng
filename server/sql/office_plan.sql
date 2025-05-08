DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '类型名称',
  `description` TEXT NOT NULL COMMENT '任务类型描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '任务类别表';

INSERT INTO `category` (`name`, `description`) VALUES
('会议安排', '组织和安排公司内部或外部的会议'),
('项目开发', '与项目相关的开发任务，包括需求分析、设计、编码等'),
('客户沟通', '与客户进行需求对接、反馈收集及问题解决'),
('文档撰写', '编写技术文档、用户手册或其他相关文档'),
('市场推广', '策划并执行市场推广活动，提升品牌知名度');

DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '任务标题',
  `code` varchar(255) NOT NULL COMMENT '任务编码',
  `category_id`  bigint(20) NOT NULL COMMENT '任务类型ID',
  `description` text DEFAULT NULL COMMENT '任务描述',
  `status` int(11) DEFAULT 1 COMMENT '任务状态（1:待办, 2:已安排, 3:已完成）',
  `importance_id`  bigint(20) DEFAULT NULL COMMENT '重要性权重设置',
  `exigency_id`  bigint(20) DEFAULT NULL COMMENT '紧急性权重设置',
  `resources_data` json DEFAULT NULL COMMENT '任务所需资源数据（JSON格式）',
  `user_data` json DEFAULT NULL COMMENT '任务所需人员数据（JSON格式）',
  `time_spend` int(11) NOT NULL COMMENT '任务所需时间分钟数',
  `deadline` TIMESTAMP NOT NULL COMMENT '任务截止时间',
  `need_meeting_room` tinyint(1) DEFAULT 0 COMMENT '是否需要会议室（1需要、0不需要）',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '任务表';

INSERT INTO `task` (`title`, `code`, `category_id`, `description`, `importance_id`, `exigency_id`, `resources_data`, `user_data`, `time_spend`, `deadline`, `create_time`) VALUES
('任务1-项目开发', UUID(), 2, '完成项目模块A的开发', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务2-项目开发', UUID(), 2, '修复模块B中的已知问题', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务3-项目开发', UUID(), 2, '编写项目文档', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务4-项目开发', UUID(), 2, '进行代码审查', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务5-项目开发', UUID(), 2, '优化系统性能', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),

('任务1-会议安排', UUID(), 1, '组织部门周会', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务2-会议安排', UUID(), 1, '客户沟通会议', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务3-会议安排', UUID(), 1, '跨部门协作会议', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务4-会议安排', UUID(), 1, '项目启动会', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务5-会议安排', UUID(), 1, '季度总结会', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),

('任务1-客户沟通', UUID(), 3, '与客户确认需求', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务2-客户沟通', UUID(), 3, '提供产品演示', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务3-客户沟通', UUID(), 3, '收集客户反馈', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务4-客户沟通', UUID(), 3, '解答客户疑问', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务5-客户沟通', UUID(), 3, '与客户签订合同', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),

('任务1-文档撰写', UUID(), 4, '撰写用户手册', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务2-文档撰写', UUID(), 4, '整理技术文档', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务3-文档撰写', UUID(), 4, '更新操作指南', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务4-文档撰写', UUID(), 4, '编写培训材料', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务5-文档撰写', UUID(), 4, '审核文档内容', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(1 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),

('任务1-市场推广', UUID(), 5, '策划市场活动', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务2-市场推广', UUID(), 5, '制作宣传材料', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务3-市场推广', UUID(), 5, '执行广告投放', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务4-市场推广', UUID(), 5, '分析推广效果', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY)),
('任务5-市场推广', UUID(), 5, '优化推广策略', FLOOR(1 + RAND() * 3), FLOOR(4 + RAND() * 3), JSON_ARRAY(FLOOR(16 + RAND() * 15), FLOOR(16 + RAND() * 15)), JSON_ARRAY(FLOOR(2 + RAND() * 30), FLOOR(2 + RAND() * 30)), FLOOR(60 + RAND() * 240), DATE_ADD(DATE(NOW()), INTERVAL FLOOR(1 + RAND() * 15) DAY), DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 30) DAY));

DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '资源名称',
  `code` varchar(255) NOT NULL COMMENT '资源编号',
  `type` int(11) DEFAULT 1 COMMENT '类型（1:会议室, 2: 设备）',
  `description` text DEFAULT NULL COMMENT '资源描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资源表';

INSERT INTO `resources` (`name`, `code`, `type`, `description`) VALUES
('会议室A', 'ROOM001', 1, '可容纳20人的大型会议室，配备投影仪和白板'),
('会议室B', 'ROOM002', 1, '可容纳10人的中型会议室，适合小型会议'),
('会议室C', 'ROOM003', 1, '可容纳5人的小型会议室，适合临时讨论'),
('会议室D', 'ROOM004', 1, '可容纳30人的多功能会议室，适合培训和讲座'),
('会议室E', 'ROOM005', 1, '可容纳15人的会议室，适合团队协作'),
('会议室F', 'ROOM006', 1, '可容纳8人的小型会议室，适合头脑风暴'),
('会议室G', 'ROOM007', 1, '可容纳12人的会议室，配备视频会议设备'),
('会议室H', 'ROOM008', 1, '可容纳25人的会议室，适合跨部门会议'),
('会议室I', 'ROOM009', 1, '可容纳6人的会议室，适合一对一会议'),
('会议室J', 'ROOM010', 1, '可容纳20人的会议室，适合产品演示'),
('会议室K', 'ROOM011', 1, '可容纳18人的会议室，适合中型团队会议'),
('会议室L', 'ROOM012', 1, '可容纳10人的会议室，适合日常沟通'),
('会议室M', 'ROOM013', 1, '可容纳4人的小型会议室，适合快速讨论'),
('会议室N', 'ROOM014', 1, '可容纳22人的会议室，适合全员会议'),
('会议室O', 'ROOM015', 1, '可容纳12人的会议室，适合培训活动'),
('笔记本电脑A', 'DEVICE001', 2, '高性能笔记本电脑，适合开发和演示'),
('笔记本电脑B', 'DEVICE002', 2, '轻薄便携笔记本电脑，适合外出使用'),
('投影仪A', 'DEVICE003', 2, '高清投影仪，支持1080P分辨率'),
('投影仪B', 'DEVICE004', 2, '便携式投影仪，适合小型会议'),
('打印机A', 'DEVICE005', 2, '高速黑白激光打印机，适合办公室使用'),
('打印机B', 'DEVICE006', 2, '彩色喷墨打印机，适合打印图表和图片'),
('电话会议设备A', 'DEVICE007', 2, '支持多方通话的电话会议设备'),
('电话会议设备B', 'DEVICE008', 2, '便携式电话会议设备，适合小型团队'),
('平板电脑A', 'DEVICE009', 2, '大屏平板电脑，适合展示和演示'),
('平板电脑B', 'DEVICE010', 2, '轻便平板电脑，适合移动办公'),
('显示器A', 'DEVICE011', 2, '4K超高清显示器，适合设计和开发'),
('显示器B', 'DEVICE012', 2, '27寸显示器，适合日常办公'),
('摄像头A', 'DEVICE013', 2, '高清摄像头，适合视频会议'),
('摄像头B', 'DEVICE014', 2, '便携式摄像头，适合远程办公'),
('麦克风A', 'DEVICE015', 2, '高灵敏度麦克风，适合录音和会议'),
('麦克风B', 'DEVICE016', 2, '降噪麦克风，适合嘈杂环境'),
('音响设备A', 'DEVICE017', 2, '高品质音响设备，适合大型会议室'),
('音响设备B', 'DEVICE018', 2, '便携式音响设备，适合小型活动'),
('路由器A', 'DEVICE019', 2, '千兆路由器，适合办公室网络'),
('路由器B', 'DEVICE020', 2, '双频路由器，适合高密度网络需求');

DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
-- `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '日程安排表';

DROP TABLE IF EXISTS `priority`;
CREATE TABLE `priority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '规则标题',
  `type` int(11) DEFAULT 1 COMMENT '类型（1:重要性, 2: 紧急性）',
  `category_id` bigint(20) DEFAULT NULL COMMENT '任务类型ID',
  `min_hours` int(11) DEFAULT NULL COMMENT '时间区间下限',
  `max_hours` int(11) DEFAULT NULL COMMENT '时间区间上限',
  `score` int(11) DEFAULT 1 COMMENT '分数',
  `weight` DECIMAL(10, 2) DEFAULT 1 COMMENT '权重',
  `description` text DEFAULT NULL COMMENT '规则描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优先级表';

INSERT INTO `priority` (`name`, `type`, `score`, `weight`, `description`, `category_id`, `min_hours`, `max_hours`) VALUES
('高', 1, 3, 0.60, '高重要性任务，对业务目标影响最大', 1, NULL, NULL),
('中', 1, 2, 0.30, '中等重要性任务，对业务有一定影响', 2, NULL, NULL),
('低', 1, 1, 0.10, '低重要性任务，对业务目标影响较小', 3, NULL, NULL),
('高', 1, 3, 0.60, '高重要性任务，对业务目标影响最大', 4, NULL, NULL),
('中', 1, 2, 0.30, '中等重要性任务，对业务有一定影响', 5, NULL, NULL),
('高', 2, 3, 0.70, '高紧急性任务，需要立即处理', NULL, NULL, 5),
('中', 2, 2, 0.20, '中等紧急性任务，可以在一定时间内完成', NULL, 5, 7),
('低', 2, 1, 0.10, '低紧急性任务，可以延后处理', NULL, 7, NULL);

DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '接收通知的用户ID',
  `message` text NOT NULL COMMENT '通知内容',
  `schedule_id` bigint(20) DEFAULT NULL COMMENT '关联日程安排ID',
  `type` int(11) DEFAULT 1 COMMENT '通知类型（1任务提醒、2系统通知等）',
  `status` int(11) DEFAULT 1 COMMENT '通知状态（1未读、2已读）',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '通知表';
