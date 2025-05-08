/*
 Navicat Premium Dump SQL

 Source Server         : 115.120.221.163
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : 115.120.221.163:3306
 Source Schema         : office_plan

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 30/04/2025 13:13:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '类型名称',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '任务类型描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '任务类别表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '会议安排', '组织和安排公司内部或外部的会议');
INSERT INTO `category` VALUES (2, '项目开发', '与项目相关的开发任务，包括需求分析、设计、编码等');
INSERT INTO `category` VALUES (3, '客户沟通', '与客户进行需求对接、反馈收集及问题解决');
INSERT INTO `category` VALUES (4, '文档撰写', '编写技术文档、用户手册或其他相关文档');
INSERT INTO `category` VALUES (5, '市场推广', '策划并执行市场推广活动，提升品牌知名度');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '接收通知的用户ID',
  `message` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '通知内容',
  `schedule_id` bigint NULL DEFAULT NULL COMMENT '关联日程安排ID',
  `type` int NULL DEFAULT 1 COMMENT '通知类型（1任务提醒、2系统通知等）',
  `status` int NULL DEFAULT 1 COMMENT '通知状态（1未读、2已读）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 4, '您有新的任务安排，请及时查看', 1, 1, 1, '2025-04-29 05:20:15');
INSERT INTO `notification` VALUES (2, 15, '您有新的任务安排，请及时查看', 1, 1, 1, '2025-04-29 05:20:15');
INSERT INTO `notification` VALUES (3, 3, '您有新的任务安排，请及时查看', 2, 1, 1, '2025-04-29 05:55:19');
INSERT INTO `notification` VALUES (4, 9, '您有新的任务安排，请及时查看', 2, 1, 1, '2025-04-29 05:55:19');
INSERT INTO `notification` VALUES (5, 18, '您有新的任务安排，请及时查看', 3, 1, 1, '2025-04-29 06:39:58');
INSERT INTO `notification` VALUES (6, 26, '您有新的任务安排，请及时查看', 3, 1, 1, '2025-04-29 06:39:58');
INSERT INTO `notification` VALUES (7, 16, '您有新的任务安排，请及时查看', 4, 1, 1, '2025-04-29 07:26:39');
INSERT INTO `notification` VALUES (8, 26, '您有新的任务安排，请及时查看', 4, 1, 1, '2025-04-29 07:26:39');
INSERT INTO `notification` VALUES (9, 21, '您有新的任务安排，请及时查看', 5, 1, 1, '2025-04-29 07:28:10');
INSERT INTO `notification` VALUES (10, 12, '您有新的任务安排，请及时查看', 5, 1, 1, '2025-04-29 07:28:10');
INSERT INTO `notification` VALUES (11, 19, '您有新的任务安排，请及时查看', 6, 1, 1, '2025-04-29 07:31:56');
INSERT INTO `notification` VALUES (12, 8, '您有新的任务安排，请及时查看', 6, 1, 1, '2025-04-29 07:31:56');
INSERT INTO `notification` VALUES (13, 9, '您有新的任务安排，请及时查看', 7, 1, 1, '2025-04-29 08:37:10');
INSERT INTO `notification` VALUES (14, 15, '您有新的任务安排，请及时查看', 7, 1, 1, '2025-04-29 08:37:10');
INSERT INTO `notification` VALUES (15, 3, '您参与的任务 \"任务1-市场推广\" 执行时间已更新', 2, 2, 1, '2025-04-30 04:46:59');
INSERT INTO `notification` VALUES (16, 9, '您参与的任务 \"任务1-市场推广\" 执行时间已更新', 2, 2, 1, '2025-04-30 04:46:59');

-- ----------------------------
-- Table structure for priority
-- ----------------------------
DROP TABLE IF EXISTS `priority`;
CREATE TABLE `priority`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '规则标题',
  `type` int NULL DEFAULT 1 COMMENT '类型（1:重要性, 2: 紧急性）',
  `category_id` bigint NULL DEFAULT NULL COMMENT '任务类型ID',
  `min_hours` int NULL DEFAULT NULL COMMENT '时间区间下限',
  `max_hours` int NULL DEFAULT NULL COMMENT '时间区间上限',
  `score` int NULL DEFAULT 1 COMMENT '分数',
  `weight` decimal(10, 2) NULL DEFAULT 1.00 COMMENT '权重',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '规则描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '优先级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of priority
-- ----------------------------
INSERT INTO `priority` VALUES (1, '高', 1, 1, NULL, NULL, 3, 0.60, '高重要性任务，对业务目标影响最大');
INSERT INTO `priority` VALUES (2, '中', 1, 2, NULL, NULL, 2, 0.30, '中等重要性任务，对业务有一定影响');
INSERT INTO `priority` VALUES (3, '低', 1, 3, NULL, NULL, 1, 0.10, '低重要性任务，对业务目标影响较小');
INSERT INTO `priority` VALUES (4, '高', 1, 4, NULL, NULL, 3, 0.60, '高重要性任务，对业务目标影响最大');
INSERT INTO `priority` VALUES (5, '中', 1, 5, NULL, NULL, 2, 0.30, '中等重要性任务，对业务有一定影响');
INSERT INTO `priority` VALUES (6, '高', 2, NULL, NULL, 5, 3, 0.70, '高紧急性任务，需要立即处理');
INSERT INTO `priority` VALUES (7, '中', 2, NULL, 5, 7, 2, 0.20, '中等紧急性任务，可以在一定时间内完成');
INSERT INTO `priority` VALUES (8, '低', 2, NULL, 7, NULL, 1, 0.10, '低紧急性任务，可以延后处理');

-- ----------------------------
-- Table structure for resources
-- ----------------------------
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '资源名称',
  `code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '资源编号',
  `type` int NULL DEFAULT 1 COMMENT '类型（1:会议室, 2: 设备）',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '资源描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resources
-- ----------------------------
INSERT INTO `resources` VALUES (1, '会议室A', 'ROOM001', 1, '可容纳20人的大型会议室，配备投影仪和白板');
INSERT INTO `resources` VALUES (2, '会议室B', 'ROOM002', 1, '可容纳10人的中型会议室，适合小型会议');
INSERT INTO `resources` VALUES (3, '会议室C', 'ROOM003', 1, '可容纳5人的小型会议室，适合临时讨论');
INSERT INTO `resources` VALUES (4, '会议室D', 'ROOM004', 1, '可容纳30人的多功能会议室，适合培训和讲座');
INSERT INTO `resources` VALUES (5, '会议室E', 'ROOM005', 1, '可容纳15人的会议室，适合团队协作');
INSERT INTO `resources` VALUES (6, '会议室F', 'ROOM006', 1, '可容纳8人的小型会议室，适合头脑风暴');
INSERT INTO `resources` VALUES (7, '会议室G', 'ROOM007', 1, '可容纳12人的会议室，配备视频会议设备');
INSERT INTO `resources` VALUES (8, '会议室H', 'ROOM008', 1, '可容纳25人的会议室，适合跨部门会议');
INSERT INTO `resources` VALUES (9, '会议室I', 'ROOM009', 1, '可容纳6人的会议室，适合一对一会议');
INSERT INTO `resources` VALUES (10, '会议室J', 'ROOM010', 1, '可容纳20人的会议室，适合产品演示');
INSERT INTO `resources` VALUES (11, '会议室K', 'ROOM011', 1, '可容纳18人的会议室，适合中型团队会议');
INSERT INTO `resources` VALUES (12, '会议室L', 'ROOM012', 1, '可容纳10人的会议室，适合日常沟通');
INSERT INTO `resources` VALUES (13, '会议室M', 'ROOM013', 1, '可容纳4人的小型会议室，适合快速讨论');
INSERT INTO `resources` VALUES (14, '会议室N', 'ROOM014', 1, '可容纳22人的会议室，适合全员会议');
INSERT INTO `resources` VALUES (15, '会议室O', 'ROOM015', 1, '可容纳12人的会议室，适合培训活动');
INSERT INTO `resources` VALUES (16, '笔记本电脑A', 'DEVICE001', 2, '高性能笔记本电脑，适合开发和演示');
INSERT INTO `resources` VALUES (17, '笔记本电脑B', 'DEVICE002', 2, '轻薄便携笔记本电脑，适合外出使用');
INSERT INTO `resources` VALUES (18, '投影仪A', 'DEVICE003', 2, '高清投影仪，支持1080P分辨率');
INSERT INTO `resources` VALUES (19, '投影仪B', 'DEVICE004', 2, '便携式投影仪，适合小型会议');
INSERT INTO `resources` VALUES (20, '打印机A', 'DEVICE005', 2, '高速黑白激光打印机，适合办公室使用');
INSERT INTO `resources` VALUES (21, '打印机B', 'DEVICE006', 2, '彩色喷墨打印机，适合打印图表和图片');
INSERT INTO `resources` VALUES (22, '电话会议设备A', 'DEVICE007', 2, '支持多方通话的电话会议设备');
INSERT INTO `resources` VALUES (23, '电话会议设备B', 'DEVICE008', 2, '便携式电话会议设备，适合小型团队');
INSERT INTO `resources` VALUES (24, '平板电脑A', 'DEVICE009', 2, '大屏平板电脑，适合展示和演示');
INSERT INTO `resources` VALUES (25, '平板电脑B', 'DEVICE010', 2, '轻便平板电脑，适合移动办公');
INSERT INTO `resources` VALUES (26, '显示器A', 'DEVICE011', 2, '4K超高清显示器，适合设计和开发');
INSERT INTO `resources` VALUES (27, '显示器B', 'DEVICE012', 2, '27寸显示器，适合日常办公');
INSERT INTO `resources` VALUES (28, '摄像头A', 'DEVICE013', 2, '高清摄像头，适合视频会议');
INSERT INTO `resources` VALUES (29, '摄像头B', 'DEVICE014', 2, '便携式摄像头，适合远程办公');
INSERT INTO `resources` VALUES (30, '麦克风A', 'DEVICE015', 2, '高灵敏度麦克风，适合录音和会议');
INSERT INTO `resources` VALUES (31, '麦克风B', 'DEVICE016', 2, '降噪麦克风，适合嘈杂环境');
INSERT INTO `resources` VALUES (32, '音响设备A', 'DEVICE017', 2, '高品质音响设备，适合大型会议室');
INSERT INTO `resources` VALUES (33, '音响设备B', 'DEVICE018', 2, '便携式音响设备，适合小型活动');
INSERT INTO `resources` VALUES (34, '路由器A', 'DEVICE019', 2, '千兆路由器，适合办公室网络');
INSERT INTO `resources` VALUES (35, '路由器B', 'DEVICE020', 2, '双频路由器，适合高密度网络需求');

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NULL DEFAULT NULL COMMENT '任务ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '日程安排表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedule
-- ----------------------------
INSERT INTO `schedule` VALUES (1, 16, '2025-04-29 00:00:00', '2025-04-29 05:20:15', NULL);
INSERT INTO `schedule` VALUES (2, 21, '2025-04-30 00:00:00', '2025-04-29 05:55:19', '2025-04-30 12:46:55');
INSERT INTO `schedule` VALUES (3, 10, '2025-04-30 00:00:00', '2025-04-29 06:39:58', NULL);
INSERT INTO `schedule` VALUES (4, 22, '2025-04-30 18:00:00', '2025-04-29 07:26:39', NULL);
INSERT INTO `schedule` VALUES (5, 20, '2025-04-30 13:00:00', '2025-04-29 07:28:10', NULL);
INSERT INTO `schedule` VALUES (6, 12, '2025-04-30 10:00:00', '2025-04-29 07:31:56', NULL);
INSERT INTO `schedule` VALUES (7, 17, '2025-04-29 00:00:00', '2025-04-29 08:37:10', '2025-04-29 02:38:00');

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '任务标题',
  `code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '任务编码',
  `category_id` bigint NOT NULL COMMENT '任务类型ID',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '任务描述',
  `status` int NULL DEFAULT 1 COMMENT '任务状态（1:待办, 2:已安排, 3:已完成）',
  `importance_id` bigint NULL DEFAULT NULL COMMENT '重要性权重设置',
  `exigency_id` bigint NULL DEFAULT NULL COMMENT '紧急性权重设置',
  `resources_data` json NULL COMMENT '任务所需资源数据（JSON格式）',
  `user_data` json NULL COMMENT '任务所需人员数据（JSON格式）',
  `time_spend` int NOT NULL COMMENT '任务所需时间分钟数',
  `deadline` timestamp NOT NULL COMMENT '任务截止时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reminder_time` datetime NULL DEFAULT NULL COMMENT '任务提醒时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (1, '任务1-项目开发', '26c85e1f-24b7-11f0-bb69-0242ac110004', 2, '完成项目模块A的开发', 1, 1, 4, '[12.0]', '[7.0, 22.0]', 277, '2025-05-06 00:00:00', '2025-04-14 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (2, '任务2-项目开发', '26c88631-24b7-11f0-bb69-0242ac110004', 2, '修复模块B中的已知问题', 1, 1, 4, '[3.0]', '[16.0, 30.0]', 122, '2025-05-07 00:00:00', '2025-04-10 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (3, '任务3-项目开发', '26c88748-24b7-11f0-bb69-0242ac110004', 2, '编写项目文档', 1, 2, 4, '[7.0]', '[16.0, 31.0]', 200, '2025-05-13 00:00:00', '2025-04-03 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (4, '任务4-项目开发', '26c887b7-24b7-11f0-bb69-0242ac110004', 2, '进行代码审查', 1, 2, 4, '[1.0]', '[23.0, 16.0]', 124, '2025-05-13 00:00:00', '2025-04-05 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (5, '任务5-项目开发', '26c8882f-24b7-11f0-bb69-0242ac110004', 2, '优化系统性能', 1, 1, 4, '[8.0]', '[29.0, 5.0]', 256, '2025-05-11 00:00:00', '2025-04-17 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (6, '任务1-会议安排', '26c88891-24b7-11f0-bb69-0242ac110004', 1, '组织部门周会', 1, 3, 4, '[13.0]', '[24.0, 5.0]', 156, '2025-05-09 00:00:00', '2025-04-27 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (7, '任务2-会议安排', '26c888f2-24b7-11f0-bb69-0242ac110004', 1, '客户沟通会议', 1, 2, 5, '[8.0]', '[4.0, 26.0]', 260, '2025-05-11 00:00:00', '2025-04-23 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (8, '任务3-会议安排', '26c8894a-24b7-11f0-bb69-0242ac110004', 1, '跨部门协作会议', 3, 3, 6, '[4.0]', '[23.0, 26.0]', 278, '2025-05-01 00:00:00', '2025-04-02 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (9, '任务4-会议安排', '26c889ae-24b7-11f0-bb69-0242ac110004', 1, '项目启动会', 5, 3, 4, '[10.0]', '[10.0, 14.0]', 122, '2025-04-30 00:00:00', '2025-04-14 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (10, '任务5-会议安排', '26c88a0d-24b7-11f0-bb69-0242ac110004', 1, '季度总结会', 1, 1, 6, '[10.0]', '[18.0, 26.0]', 172, '2025-05-13 00:00:00', '2025-03-31 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (11, '任务1-客户沟通', '26c88a80-24b7-11f0-bb69-0242ac110004', 3, '与客户确认需求', 1, 1, 6, '[27.0, 20.0]', '[6.0, 30.0]', 110, '2025-05-03 00:00:00', '2025-04-11 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (12, '任务2-客户沟通', '26c88b1a-24b7-11f0-bb69-0242ac110004', 3, '提供产品演示', 4, 1, 6, '[18.0, 19.0]', '[19.0, 8.0]', 133, '2025-05-13 00:00:00', '2025-04-11 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (13, '任务3-客户沟通', '26c88bb0-24b7-11f0-bb69-0242ac110004', 3, '收集客户反馈', 1, 1, 5, '[30.0, 19.0]', '[8.0, 12.0]', 87, '2025-05-08 00:00:00', '2025-04-18 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (14, '任务4-客户沟通', '26c88c1f-24b7-11f0-bb69-0242ac110004', 3, '解答客户疑问', 1, 1, 5, '[22.0, 23.0]', '[5.0, 4.0]', 88, '2025-05-04 00:00:00', '2025-04-20 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (15, '任务5-客户沟通', '26c88c81-24b7-11f0-bb69-0242ac110004', 3, '与客户签订合同', 3, 2, 5, '[26.0, 17.0]', '[19.0, 19.0]', 100, '2025-05-01 00:00:00', '2025-04-01 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (16, '任务1-文档撰写', '26c88ce4-24b7-11f0-bb69-0242ac110004', 4, '撰写用户手册', 2, 1, 6, '[10.0]', '[4.0, 15.0]', 75, '2025-05-14 00:00:00', '2025-04-11 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (17, '任务2-文档撰写', '26c88d3c-24b7-11f0-bb69-0242ac110004', 4, '整理技术文档', 5, 1, 6, '[5.0]', '[9.0, 15.0]', 158, '2025-05-11 00:00:00', '2025-04-14 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (18, '任务3-文档撰写', '26c88d98-24b7-11f0-bb69-0242ac110004', 4, '更新操作指南', 1, 1, 6, '[1.0]', '[25.0, 26.0]', 262, '2025-05-10 00:00:00', '2025-04-25 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (19, '任务4-文档撰写', '26c88df0-24b7-11f0-bb69-0242ac110004', 4, '编写培训材料', 1, 2, 5, '[4.0]', '[26.0, 14.0]', 187, '2025-05-06 00:00:00', '2025-04-09 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (20, '任务5-文档撰写', '26c88e48-24b7-11f0-bb69-0242ac110004', 4, '审核文档内容', 2, 3, 5, '[5.0]', '[21.0, 12.0]', 245, '2025-05-12 00:00:00', '2025-04-02 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (21, '任务1-市场推广', '26c88e9f-24b7-11f0-bb69-0242ac110004', 5, '策划市场活动', 5, 3, 5, '[21.0, 16.0]', '[9.0, 3.0]', 176, '2025-05-04 00:00:00', '2025-04-28 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (22, '任务2-市场推广', '26c88ef7-24b7-11f0-bb69-0242ac110004', 5, '制作宣传材料', 2, 1, 6, '[16.0, 24.0]', '[16.0, 26.0]', 197, '2025-05-06 00:00:00', '2025-04-13 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (23, '任务3-市场推广', '26c88f5e-24b7-11f0-bb69-0242ac110004', 5, '执行广告投放', 1, 1, 5, '[17.0, 28.0]', '[28.0, 30.0]', 93, '2025-05-12 00:00:00', '2025-04-10 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (24, '任务4-市场推广', '26c88fb6-24b7-11f0-bb69-0242ac110004', 5, '分析推广效果', 1, 3, 5, '[21.0, 24.0]', '[18.0, 4.0]', 238, '2025-05-07 00:00:00', '2025-04-21 05:02:29', '2025-04-29 05:02:29', NULL);
INSERT INTO `task` VALUES (25, '任务5-市场推广', '26c89009-24b7-11f0-bb69-0242ac110004', 5, '优化推广策略', 1, 3, 4, '[22.0, 25.0]', '[30.0, 25.0]', 94, '2025-05-04 00:00:00', '2025-04-22 05:02:29', '2025-04-29 05:02:29', NULL);

-- ----------------------------
-- Table structure for ums_admin
-- ----------------------------
DROP TABLE IF EXISTS `ums_admin`;
CREATE TABLE `ums_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `icon` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `address` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `note` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '部门职位等信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` int NULL DEFAULT 1 COMMENT '帐号启用状态：0->禁用；1->启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_admin
-- ----------------------------
INSERT INTO `ums_admin` VALUES (1, 'admin', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'test@qq.com', '管理员', '男', '2025-03-27', 18, '13088886666', '北京市', NULL, '2025-04-29 05:01:59', '2025-04-30 13:03:13', 1);
INSERT INTO `ums_admin` VALUES (2, 'user1', '$2a$10$xOrYArljq0VyGJVhtrEZyug2fF97WY5hSArA083r/kybd.CaBKBdu', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user1@example.com', '张伟', '男', '1990-01-15', 35, '13800000001', '北京市朝阳区望京街道', '技术部后端开发工程师', '2025-04-29 05:01:59', '2025-04-30 13:07:02', 1);
INSERT INTO `ums_admin` VALUES (3, 'user2', '$2a$10$VIHX0gPZ.io4zOQ0T4uqwuDPr978eg83c31NjlJyM74/npAvwysDq', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user2@example.com', '李娜', '女', '1992-05-20', 33, '13800000002', '上海市浦东新区张江高科技园区', '市场部营销专员', '2025-04-29 05:01:59', '2025-04-30 13:03:01', 1);
INSERT INTO `ums_admin` VALUES (4, 'user3', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user3@example.com', '王强', '男', '1988-07-10', 37, '13800000003', '深圳市南山区科技园', '研发部前端开发工程师', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (5, 'user4', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user4@example.com', '赵敏', '女', '1995-03-25', 30, '13800000004', '广州市天河区珠江新城', '人力资源部招聘经理', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (6, 'user5', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user5@example.com', '刘洋', '男', '1993-11-05', 32, '13800000005', '杭州市西湖区文三路', '产品部产品经理', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (7, 'user6', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user6@example.com', '孙丽', '女', '1991-09-12', 34, '13800000006', '成都市武侯区天府大道', '财务部会计', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (8, 'user7', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user7@example.com', '周杰', '男', '1989-04-18', 36, '13800000007', '南京市鼓楼区新街口', '运营部运营专员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (9, 'user8', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user8@example.com', '吴芳', '女', '1994-08-30', 31, '13800000008', '武汉市洪山区光谷广场', '设计部UI设计师', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (10, 'user9', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user9@example.com', '郑凯', '男', '1996-02-14', 29, '13800000009', '西安市雁塔区高新路', '测试部测试工程师', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (11, 'user10', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user10@example.com', '陈静', '女', '1997-06-22', 28, '13800000010', '重庆市渝中区解放碑', '客服部客服主管', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (12, 'user11', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user11@example.com', '黄磊', '男', '1987-12-03', 38, '13800000011', '天津市和平区南京路', '运维部系统管理员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (13, 'user12', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user12@example.com', '徐婷', '女', '1998-09-08', 27, '13800000012', '青岛市市南区五四广场', '行政部行政助理', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (14, 'user13', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user13@example.com', '马超', '男', '1985-03-17', 40, '13800000013', '苏州市工业园区金鸡湖', '销售部大客户经理', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (15, 'user14', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user14@example.com', '杨雪', '女', '1990-07-28', 35, '13800000014', '东莞市南城区鸿福路', '法务部法律顾问', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (16, 'user15', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user15@example.com', '朱军', '男', '1986-10-19', 39, '13800000015', '厦门市思明区中山路', 'IT部技术支持', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (17, 'user16', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user16@example.com', '许晴', '女', '1992-11-11', 33, '13800000016', '长沙市岳麓区麓山南路', '品牌部品牌策划', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (18, 'user17', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user17@example.com', '何平', '男', '1989-02-24', 36, '13800000017', '合肥市包河区滨湖新区', '物流部仓储主管', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (19, 'user18', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user18@example.com', '郭涛', '男', '1993-05-05', 32, '13800000018', '济南市历下区泉城路', '采购部采购专员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (20, 'user19', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user19@example.com', '林琳', '女', '1994-08-16', 31, '13800000019', '昆明市五华区翠湖公园', '培训部培训讲师', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (21, 'user20', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user20@example.com', '高翔', '男', '1996-09-27', 29, '13800000020', '南宁市青秀区民族大道', '数据分析部数据分析师', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (22, 'user21', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user21@example.com', '谢娜', '女', '1997-01-08', 28, '13800000021', '贵阳市云岩区喷水池', '公关部公关经理', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (23, 'user22', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user22@example.com', '宋佳', '女', '1995-04-19', 30, '13800000022', '兰州市城关区东方红广场', '战略部战略分析师', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (24, 'user23', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user23@example.com', '罗刚', '男', '1988-06-22', 37, '13800000023', '福州市鼓楼区五一广场', '质量部质量管理专员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (25, 'user24', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user24@example.com', '唐敏', '女', '1991-08-03', 34, '13800000024', '南昌市东湖区八一广场', '媒体部内容编辑', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (26, 'user25', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user25@example.com', '邓超', '男', '1987-12-14', 38, '13800000025', '沈阳市和平区太原街', '风控部风险控制专员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (27, 'user26', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user26@example.com', '冯雷', '男', '1990-03-29', 35, '13800000026', '哈尔滨市南岗区中央大街', '审计部审计专员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (28, 'user27', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user27@example.com', '韩梅', '女', '1992-07-18', 33, '13800000027', '长春市朝阳区人民大街', '投资部投资经理', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (29, 'user28', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user28@example.com', '曹阳', '男', '1986-11-07', 39, '13800000028', '石家庄市长安区北国商城', '合规部合规专员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (30, 'user29', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user29@example.com', '潘婷', '女', '1994-02-20', 31, '13800000029', '太原市小店区长风街', '创新部创新研究员', '2025-04-29 05:01:59', NULL, 1);
INSERT INTO `ums_admin` VALUES (31, 'user30', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user30@example.com', '汪洋', '男', '1995-06-05', 30, '13800000030', '郑州市金水区二七广场', '技术部架构师', '2025-04-29 05:01:59', NULL, 1);

-- ----------------------------
-- Table structure for ums_admin_login_log
-- ----------------------------
DROP TABLE IF EXISTS `ums_admin_login_log`;
CREATE TABLE `ums_admin_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `ip` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `address` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `user_agent` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '浏览器登录类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台用户登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_admin_login_log
-- ----------------------------
INSERT INTO `ums_admin_login_log` VALUES (1, 1, '2025-04-29 13:10:54', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (2, 2, '2025-04-29 13:12:02', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (3, 1, '2025-04-29 13:12:37', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (4, 1, '2025-04-29 14:29:43', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (5, 1, '2025-04-29 15:06:21', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (6, 1, '2025-04-29 15:12:09', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (7, 1, '2025-04-29 15:54:25', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (8, 1, '2025-04-29 16:33:47', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (9, 1, '2025-04-29 17:15:34', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (10, 1, '2025-04-29 17:31:00', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (11, 1, '2025-04-30 12:29:51', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (12, 1, '2025-04-30 12:48:30', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (13, 2, '2025-04-30 12:48:58', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (14, 1, '2025-04-30 12:49:37', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (15, 3, '2025-04-30 12:52:16', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (16, 3, '2025-04-30 12:52:28', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (17, 1, '2025-04-30 12:54:57', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (18, 3, '2025-04-30 13:03:01', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (19, 1, '2025-04-30 13:03:13', '0:0:0:0:0:0:0:1', NULL, NULL);
INSERT INTO `ums_admin_login_log` VALUES (20, 2, '2025-04-30 13:07:02', '0:0:0:0:0:0:0:1', NULL, NULL);

-- ----------------------------
-- Table structure for ums_admin_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_admin_role_relation`;
CREATE TABLE `ums_admin_role_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NULL DEFAULT NULL,
  `role_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台用户和角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_admin_role_relation
-- ----------------------------
INSERT INTO `ums_admin_role_relation` VALUES (1, 1, 1);
INSERT INTO `ums_admin_role_relation` VALUES (2, 2, 2);
INSERT INTO `ums_admin_role_relation` VALUES (3, 3, 2);
INSERT INTO `ums_admin_role_relation` VALUES (4, 4, 2);
INSERT INTO `ums_admin_role_relation` VALUES (5, 5, 2);
INSERT INTO `ums_admin_role_relation` VALUES (6, 6, 2);
INSERT INTO `ums_admin_role_relation` VALUES (7, 7, 2);
INSERT INTO `ums_admin_role_relation` VALUES (8, 8, 2);
INSERT INTO `ums_admin_role_relation` VALUES (9, 9, 2);
INSERT INTO `ums_admin_role_relation` VALUES (10, 10, 2);
INSERT INTO `ums_admin_role_relation` VALUES (11, 11, 2);
INSERT INTO `ums_admin_role_relation` VALUES (12, 12, 2);
INSERT INTO `ums_admin_role_relation` VALUES (13, 13, 2);
INSERT INTO `ums_admin_role_relation` VALUES (14, 14, 2);
INSERT INTO `ums_admin_role_relation` VALUES (15, 15, 2);
INSERT INTO `ums_admin_role_relation` VALUES (16, 16, 2);
INSERT INTO `ums_admin_role_relation` VALUES (17, 17, 2);
INSERT INTO `ums_admin_role_relation` VALUES (18, 18, 2);
INSERT INTO `ums_admin_role_relation` VALUES (19, 19, 2);
INSERT INTO `ums_admin_role_relation` VALUES (20, 20, 2);
INSERT INTO `ums_admin_role_relation` VALUES (21, 21, 2);
INSERT INTO `ums_admin_role_relation` VALUES (22, 22, 2);
INSERT INTO `ums_admin_role_relation` VALUES (23, 23, 2);
INSERT INTO `ums_admin_role_relation` VALUES (24, 24, 2);
INSERT INTO `ums_admin_role_relation` VALUES (25, 25, 2);
INSERT INTO `ums_admin_role_relation` VALUES (26, 26, 2);
INSERT INTO `ums_admin_role_relation` VALUES (27, 27, 2);
INSERT INTO `ums_admin_role_relation` VALUES (28, 28, 2);
INSERT INTO `ums_admin_role_relation` VALUES (29, 29, 2);
INSERT INTO `ums_admin_role_relation` VALUES (30, 30, 2);
INSERT INTO `ums_admin_role_relation` VALUES (31, 31, 2);

-- ----------------------------
-- Table structure for ums_menu
-- ----------------------------
DROP TABLE IF EXISTS `ums_menu`;
CREATE TABLE `ums_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `title` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `level` int NULL DEFAULT NULL COMMENT '菜单级数',
  `sort` int NULL DEFAULT NULL COMMENT '菜单排序',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '前端名称',
  `icon` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '前端图标',
  `hidden` int NULL DEFAULT NULL COMMENT '前端隐藏',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 204 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_menu
-- ----------------------------
INSERT INTO `ums_menu` VALUES (1, 0, '2025-03-27 16:29:13', '权限', 0, 0, 'ums', 'ums', 0);
INSERT INTO `ums_menu` VALUES (2, 1, '2025-03-27 16:29:51', '用户列表', 1, 0, 'admin', 'ums-admin', 0);
INSERT INTO `ums_menu` VALUES (3, 1, '2025-03-27 16:30:13', '角色列表', 1, 0, 'role', 'ums-role', 0);
INSERT INTO `ums_menu` VALUES (4, 1, '2025-03-27 16:30:53', '菜单列表', 1, 0, 'menu', 'ums-menu', 0);
INSERT INTO `ums_menu` VALUES (5, 1, '2025-03-27 16:31:13', '资源列表', 1, 0, 'resource', 'ums-resource', 0);
INSERT INTO `ums_menu` VALUES (100, 0, '2025-03-27 16:31:13', '类型管理', 0, 0, 'category', 'ums-role', 0);
INSERT INTO `ums_menu` VALUES (101, 0, '2025-03-27 16:31:13', '任务管理', 0, 0, 'task', 'calendar', 0);
INSERT INTO `ums_menu` VALUES (102, 0, '2025-03-27 16:31:13', '资源管理', 0, 0, 'resources', 'ums-menu', 0);
INSERT INTO `ums_menu` VALUES (103, 0, '2025-03-27 16:31:13', '日程安排', 0, 0, 'schedule', 'attendance', 0);
INSERT INTO `ums_menu` VALUES (104, 0, '2025-03-27 16:31:13', '权重管理', 0, 0, 'priority', 'example', 0);
INSERT INTO `ums_menu` VALUES (105, 0, '2025-03-27 16:31:13', '通知管理', 0, 0, 'notification', 'notice', 0);
INSERT INTO `ums_menu` VALUES (106, 0, '2025-03-27 16:31:13', '统计报表', 0, 0, 'chart', 'total-week', 0);
INSERT INTO `ums_menu` VALUES (200, 0, '2025-03-27 16:31:13', '个人信息', 0, 0, 'userInfo', 'ums-admin', 0);
INSERT INTO `ums_menu` VALUES (201, 0, '2025-03-27 16:31:13', '日程安排', 0, 0, 'userSchedule', 'attendance', 0);
INSERT INTO `ums_menu` VALUES (202, 0, '2025-03-27 16:31:13', '我的通知', 0, 0, 'userNotification', 'notice', 0);
INSERT INTO `ums_menu` VALUES (203, 0, '2025-03-27 16:31:13', '任务管理', 0, 0, 'userTask', 'calendar', 0);

-- ----------------------------
-- Table structure for ums_resource
-- ----------------------------
DROP TABLE IF EXISTS `ums_resource`;
CREATE TABLE `ums_resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '资源名称',
  `url` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '资源URL',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `category_id` bigint NULL DEFAULT NULL COMMENT '资源分类ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_resource
-- ----------------------------
INSERT INTO `ums_resource` VALUES (1, '2025-03-27 16:47:34', '后台用户管理', '/admin/**', '', 1);
INSERT INTO `ums_resource` VALUES (2, '2025-03-27 16:48:24', '后台用户角色管理', '/role/**', '', 1);
INSERT INTO `ums_resource` VALUES (3, '2025-03-27 16:48:48', '后台菜单管理', '/menu/**', '', 1);
INSERT INTO `ums_resource` VALUES (4, '2025-03-27 16:49:18', '后台资源分类管理', '/resourceCategory/**', '', 1);
INSERT INTO `ums_resource` VALUES (5, '2025-03-27 16:49:45', '后台资源管理', '/resource/**', '', 1);

-- ----------------------------
-- Table structure for ums_resource_category
-- ----------------------------
DROP TABLE IF EXISTS `ums_resource_category`;
CREATE TABLE `ums_resource_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '资源分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_resource_category
-- ----------------------------
INSERT INTO `ums_resource_category` VALUES (1, '2025-03-27 10:23:04', '权限模块', 0);

-- ----------------------------
-- Table structure for ums_role
-- ----------------------------
DROP TABLE IF EXISTS `ums_role`;
CREATE TABLE `ums_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `admin_count` int NULL DEFAULT NULL COMMENT '后台用户数量',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` int NULL DEFAULT 1 COMMENT '启用状态：0->禁用；1->启用',
  `sort` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_role
-- ----------------------------
INSERT INTO `ums_role` VALUES (1, '平台管理员', '平台权限', 0, '2025-03-27 16:50:37', 1, 0);
INSERT INTO `ums_role` VALUES (2, '普通用户', '普通用户权限', 0, '2025-03-27 16:50:37', 1, 0);

-- ----------------------------
-- Table structure for ums_role_menu_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_role_menu_relation`;
CREATE TABLE `ums_role_menu_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint NULL DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 204 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台角色菜单关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_role_menu_relation
-- ----------------------------
INSERT INTO `ums_role_menu_relation` VALUES (1, 1, 1);
INSERT INTO `ums_role_menu_relation` VALUES (2, 1, 2);
INSERT INTO `ums_role_menu_relation` VALUES (3, 1, 3);
INSERT INTO `ums_role_menu_relation` VALUES (4, 1, 4);
INSERT INTO `ums_role_menu_relation` VALUES (5, 1, 5);
INSERT INTO `ums_role_menu_relation` VALUES (100, 1, 100);
INSERT INTO `ums_role_menu_relation` VALUES (101, 1, 101);
INSERT INTO `ums_role_menu_relation` VALUES (102, 1, 102);
INSERT INTO `ums_role_menu_relation` VALUES (103, 1, 103);
INSERT INTO `ums_role_menu_relation` VALUES (104, 1, 104);
INSERT INTO `ums_role_menu_relation` VALUES (105, 1, 105);
INSERT INTO `ums_role_menu_relation` VALUES (106, 1, 106);
INSERT INTO `ums_role_menu_relation` VALUES (200, 2, 200);
INSERT INTO `ums_role_menu_relation` VALUES (201, 2, 201);
INSERT INTO `ums_role_menu_relation` VALUES (202, 2, 202);
INSERT INTO `ums_role_menu_relation` VALUES (203, 2, 203);

-- ----------------------------
-- Table structure for ums_role_resource_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_role_resource_relation`;
CREATE TABLE `ums_role_resource_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '资源ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '后台角色资源关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_role_resource_relation
-- ----------------------------
INSERT INTO `ums_role_resource_relation` VALUES (1, 1, 1);
INSERT INTO `ums_role_resource_relation` VALUES (2, 1, 2);
INSERT INTO `ums_role_resource_relation` VALUES (3, 1, 3);
INSERT INTO `ums_role_resource_relation` VALUES (4, 1, 4);
INSERT INTO `ums_role_resource_relation` VALUES (5, 1, 5);
INSERT INTO `ums_role_resource_relation` VALUES (6, 2, 1);
INSERT INTO `ums_role_resource_relation` VALUES (7, 2, 2);
INSERT INTO `ums_role_resource_relation` VALUES (8, 2, 3);
INSERT INTO `ums_role_resource_relation` VALUES (9, 2, 4);
INSERT INTO `ums_role_resource_relation` VALUES (10, 2, 5);

SET FOREIGN_KEY_CHECKS = 1;
