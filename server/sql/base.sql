/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : office_plan

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001
*/

create database IF NOT EXISTS `office_plan` default character set utf8mb4 collate utf8mb4_general_ci;

USE office_plan;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ums_admin
-- ----------------------------
DROP TABLE IF EXISTS `ums_admin`;
CREATE TABLE `ums_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` text DEFAULT NULL,
  `icon` varchar(500) DEFAULT NULL COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(200) DEFAULT NULL COMMENT '昵称',
  `gender` varchar(16) DEFAULT NULL COMMENT '性别',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `age` int(10) DEFAULT NULL COMMENT '年龄',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系方式',
  `address` varchar(500) DEFAULT NULL COMMENT '家庭住址',
  `note` varchar(500) DEFAULT NULL COMMENT '部门职位等信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` int(1) DEFAULT '1' COMMENT '帐号启用状态：0->禁用；1->启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of ums_admin
-- ----------------------------
-- INSERT INTO `ums_admin` VALUES ('1', 'admin', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'test@qq.com', '管理员', '男', '2025-03-27', 18, '13088886666', '北京市', null, '2025-03-27 13:55:30', '2025-03-27 13:55:39', '1');

INSERT INTO `ums_admin` (`username`, `password`, `icon`, `email`, `nick_name`, `gender`, `birthday`, `age`, `phone`, `address`, `note`, `create_time`) VALUES
('admin', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'test@qq.com', '管理员', '男', '2025-03-27', 18, '13088886666', '北京市', null, NOW()),
('user1', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user1@example.com', '张伟', '男', '1990-01-15', 35, '13800000001', '北京市朝阳区望京街道', '技术部后端开发工程师', NOW()),
('user2', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user2@example.com', '李娜', '女', '1992-05-20', 33, '13800000002', '上海市浦东新区张江高科技园区', '市场部营销专员', NOW()),
('user3', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user3@example.com', '王强', '男', '1988-07-10', 37, '13800000003', '深圳市南山区科技园', '研发部前端开发工程师', NOW()),
('user4', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user4@example.com', '赵敏', '女', '1995-03-25', 30, '13800000004', '广州市天河区珠江新城', '人力资源部招聘经理', NOW()),
('user5', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user5@example.com', '刘洋', '男', '1993-11-05', 32, '13800000005', '杭州市西湖区文三路', '产品部产品经理', NOW()),
('user6', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user6@example.com', '孙丽', '女', '1991-09-12', 34, '13800000006', '成都市武侯区天府大道', '财务部会计', NOW()),
('user7', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user7@example.com', '周杰', '男', '1989-04-18', 36, '13800000007', '南京市鼓楼区新街口', '运营部运营专员', NOW()),
('user8', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user8@example.com', '吴芳', '女', '1994-08-30', 31, '13800000008', '武汉市洪山区光谷广场', '设计部UI设计师', NOW()),
('user9', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user9@example.com', '郑凯', '男', '1996-02-14', 29, '13800000009', '西安市雁塔区高新路', '测试部测试工程师', NOW()),
('user10', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user10@example.com', '陈静', '女', '1997-06-22', 28, '13800000010', '重庆市渝中区解放碑', '客服部客服主管', NOW()),

('user11', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user11@example.com', '黄磊', '男', '1987-12-03', 38, '13800000011', '天津市和平区南京路', '运维部系统管理员', NOW()),
('user12', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user12@example.com', '徐婷', '女', '1998-09-08', 27, '13800000012', '青岛市市南区五四广场', '行政部行政助理', NOW()),
('user13', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user13@example.com', '马超', '男', '1985-03-17', 40, '13800000013', '苏州市工业园区金鸡湖', '销售部大客户经理', NOW()),
('user14', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user14@example.com', '杨雪', '女', '1990-07-28', 35, '13800000014', '东莞市南城区鸿福路', '法务部法律顾问', NOW()),
('user15', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user15@example.com', '朱军', '男', '1986-10-19', 39, '13800000015', '厦门市思明区中山路', 'IT部技术支持', NOW()),

('user16', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user16@example.com', '许晴', '女', '1992-11-11', 33, '13800000016', '长沙市岳麓区麓山南路', '品牌部品牌策划', NOW()),
('user17', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user17@example.com', '何平', '男', '1989-02-24', 36, '13800000017', '合肥市包河区滨湖新区', '物流部仓储主管', NOW()),
('user18', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user18@example.com', '郭涛', '男', '1993-05-05', 32, '13800000018', '济南市历下区泉城路', '采购部采购专员', NOW()),
('user19', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user19@example.com', '林琳', '女', '1994-08-16', 31, '13800000019', '昆明市五华区翠湖公园', '培训部培训讲师', NOW()),
('user20', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user20@example.com', '高翔', '男', '1996-09-27', 29, '13800000020', '南宁市青秀区民族大道', '数据分析部数据分析师', NOW()),

('user21', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user21@example.com', '谢娜', '女', '1997-01-08', 28, '13800000021', '贵阳市云岩区喷水池', '公关部公关经理', NOW()),
('user22', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user22@example.com', '宋佳', '女', '1995-04-19', 30, '13800000022', '兰州市城关区东方红广场', '战略部战略分析师', NOW()),
('user23', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user23@example.com', '罗刚', '男', '1988-06-22', 37, '13800000023', '福州市鼓楼区五一广场', '质量部质量管理专员', NOW()),
('user24', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user24@example.com', '唐敏', '女', '1991-08-03', 34, '13800000024', '南昌市东湖区八一广场', '媒体部内容编辑', NOW()),
('user25', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user25@example.com', '邓超', '男', '1987-12-14', 38, '13800000025', '沈阳市和平区太原街', '风控部风险控制专员', NOW()),

('user26', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user26@example.com', '冯雷', '男', '1990-03-29', 35, '13800000026', '哈尔滨市南岗区中央大街', '审计部审计专员', NOW()),
('user27', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user27@example.com', '韩梅', '女', '1992-07-18', 33, '13800000027', '长春市朝阳区人民大街', '投资部投资经理', NOW()),
('user28', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user28@example.com', '曹阳', '男', '1986-11-07', 39, '13800000028', '石家庄市长安区北国商城', '合规部合规专员', NOW()),
('user29', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user29@example.com', '潘婷', '女', '1994-02-20', 31, '13800000029', '太原市小店区长风街', '创新部创新研究员', NOW()),
('user30', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', 'user30@example.com', '汪洋', '男', '1995-06-05', 30, '13800000030', '郑州市金水区二七广场', '技术部架构师', NOW());

-- ----------------------------
-- Table structure for ums_admin_login_log
-- ----------------------------
DROP TABLE IF EXISTS `ums_admin_login_log`;
CREATE TABLE `ums_admin_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `user_agent` varchar(100) DEFAULT NULL COMMENT '浏览器登录类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台用户登录日志表';

-- ----------------------------
-- Records of ums_admin_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for ums_admin_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_admin_role_relation`;
CREATE TABLE `ums_admin_role_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台用户和角色关系表';

-- ----------------------------
-- Records of ums_admin_role_relation
-- ----------------------------
INSERT INTO `ums_admin_role_relation` VALUES ('1', '1', '1');
INSERT INTO `ums_admin_role_relation` VALUES
(2, 2, 2),
(3, 3, 2),
(4, 4, 2),
(5, 5, 2),
(6, 6, 2),
(7, 7, 2),
(8, 8, 2),
(9, 9, 2),
(10, 10, 2),
(11, 11, 2),
(12, 12, 2),
(13, 13, 2),
(14, 14, 2),
(15, 15, 2),
(16, 16, 2),
(17, 17, 2),
(18, 18, 2),
(19, 19, 2),
(20, 20, 2),
(21, 21, 2),
(22, 22, 2),
(23, 23, 2),
(24, 24, 2),
(25, 25, 2),
(26, 26, 2),
(27, 27, 2),
(28, 28, 2),
(29, 29, 2),
(30, 30, 2),
(31, 31, 2);

-- ----------------------------
-- Table structure for ums_menu
-- ----------------------------
DROP TABLE IF EXISTS `ums_menu`;
CREATE TABLE `ums_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `title` varchar(100) DEFAULT NULL COMMENT '菜单名称',
  `level` int(4) DEFAULT NULL COMMENT '菜单级数',
  `sort` int(4) DEFAULT NULL COMMENT '菜单排序',
  `name` varchar(100) DEFAULT NULL COMMENT '前端名称',
  `icon` varchar(200) DEFAULT NULL COMMENT '前端图标',
  `hidden` int(1) DEFAULT NULL COMMENT '前端隐藏',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台菜单表';

-- ----------------------------
-- Records of ums_menu
-- ----------------------------
INSERT INTO `ums_menu` VALUES ('1', '0', '2025-03-27 16:29:13', '权限', '0', '0', 'ums', 'ums', '0');
INSERT INTO `ums_menu` VALUES ('2', '1', '2025-03-27 16:29:51', '用户列表', '1', '0', 'admin', 'ums-admin', '0');
INSERT INTO `ums_menu` VALUES ('3', '1', '2025-03-27 16:30:13', '角色列表', '1', '0', 'role', 'ums-role', '0');
INSERT INTO `ums_menu` VALUES ('4', '1', '2025-03-27 16:30:53', '菜单列表', '1', '0', 'menu', 'ums-menu', '0');
INSERT INTO `ums_menu` VALUES ('5', '1', '2025-03-27 16:31:13', '资源列表', '1', '0', 'resource', 'ums-resource', '0');

INSERT INTO `ums_menu` VALUES ('100', '0', '2025-03-27 16:31:13', '类型管理', '0', '0', 'category', 'ums-role', '0');
INSERT INTO `ums_menu` VALUES ('101', '0', '2025-03-27 16:31:13', '任务管理', '0', '0', 'task', 'calendar', '0');
INSERT INTO `ums_menu` VALUES ('102', '0', '2025-03-27 16:31:13', '资源管理', '0', '0', 'resources', 'ums-menu', '0');
INSERT INTO `ums_menu` VALUES ('103', '0', '2025-03-27 16:31:13', '日程安排', '0', '0', 'schedule', 'attendance', '0');
INSERT INTO `ums_menu` VALUES ('104', '0', '2025-03-27 16:31:13', '权重管理', '0', '0', 'priority', 'example', '0');
INSERT INTO `ums_menu` VALUES ('105', '0', '2025-03-27 16:31:13', '通知管理', '0', '0', 'notification', 'notice', '0');
INSERT INTO `ums_menu` VALUES ('106', '0', '2025-03-27 16:31:13', '统计报表', '0', '0', 'chart', 'total-week', '0');

INSERT INTO `ums_menu` VALUES ('200', '0', '2025-03-27 16:31:13', '个人信息', '0', '0', 'userInfo', 'ums-admin', '0');
INSERT INTO `ums_menu` VALUES ('201', '0', '2025-03-27 16:31:13', '日程安排', '0', '0', 'userSchedule', 'attendance', '0');
INSERT INTO `ums_menu` VALUES ('202', '0', '2025-03-27 16:31:13', '我的通知', '0', '0', 'userNotification', 'notice', '0');
INSERT INTO `ums_menu` VALUES ('203', '0', '2025-03-27 16:31:13', '任务管理', '0', '0', 'userTask', 'calendar', '0');

-- ----------------------------
-- Table structure for ums_resource
-- ----------------------------
DROP TABLE IF EXISTS `ums_resource`;
CREATE TABLE `ums_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `name` varchar(200) DEFAULT NULL COMMENT '资源名称',
  `url` varchar(200) DEFAULT NULL COMMENT '资源URL',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `category_id` bigint(20) DEFAULT NULL COMMENT '资源分类ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台资源表';

-- ----------------------------
-- Records of ums_resource
-- ----------------------------
INSERT INTO `ums_resource` VALUES ('1', '2025-03-27 16:47:34', '后台用户管理', '/admin/**', '', '1');
INSERT INTO `ums_resource` VALUES ('2', '2025-03-27 16:48:24', '后台用户角色管理', '/role/**', '', '1');
INSERT INTO `ums_resource` VALUES ('3', '2025-03-27 16:48:48', '后台菜单管理', '/menu/**', '', '1');
INSERT INTO `ums_resource` VALUES ('4', '2025-03-27 16:49:18', '后台资源分类管理', '/resourceCategory/**', '', '1');
INSERT INTO `ums_resource` VALUES ('5', '2025-03-27 16:49:45', '后台资源管理', '/resource/**', '', '1');

-- ----------------------------
-- Table structure for ums_resource_category
-- ----------------------------
DROP TABLE IF EXISTS `ums_resource_category`;
CREATE TABLE `ums_resource_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `name` varchar(200) DEFAULT NULL COMMENT '分类名称',
  `sort` int(4) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资源分类表';

-- ----------------------------
-- Records of ums_resource_category
-- ----------------------------
INSERT INTO `ums_resource_category` VALUES ('1', '2025-03-27 10:23:04', '权限模块', '0');

-- ----------------------------
-- Table structure for ums_role
-- ----------------------------
DROP TABLE IF EXISTS `ums_role`;
CREATE TABLE `ums_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `admin_count` int(11) DEFAULT NULL COMMENT '后台用户数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(1) DEFAULT '1' COMMENT '启用状态：0->禁用；1->启用',
  `sort` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台用户角色表';

-- ----------------------------
-- Records of ums_role
-- ----------------------------
INSERT INTO `ums_role` VALUES ('1', '平台管理员', '平台权限', '0', '2025-03-27 16:50:37', '1', '0');
INSERT INTO `ums_role` VALUES ('2', '普通用户', '普通用户权限', '0', '2025-03-27 16:50:37', '1', '0');

-- ----------------------------
-- Table structure for ums_role_menu_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_role_menu_relation`;
CREATE TABLE `ums_role_menu_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台角色菜单关系表';

-- ----------------------------
-- Records of ums_role_menu_relation
-- ----------------------------
INSERT INTO `ums_role_menu_relation` VALUES ('1', '1', '1');
INSERT INTO `ums_role_menu_relation` VALUES ('2', '1', '2');
INSERT INTO `ums_role_menu_relation` VALUES ('3', '1', '3');
INSERT INTO `ums_role_menu_relation` VALUES ('4', '1', '4');
INSERT INTO `ums_role_menu_relation` VALUES ('5', '1', '5');

INSERT INTO `ums_role_menu_relation` VALUES ('100', '1', '100');
INSERT INTO `ums_role_menu_relation` VALUES ('101', '1', '101');
INSERT INTO `ums_role_menu_relation` VALUES ('102', '1', '102');
INSERT INTO `ums_role_menu_relation` VALUES ('103', '1', '103');
INSERT INTO `ums_role_menu_relation` VALUES ('104', '1', '104');
INSERT INTO `ums_role_menu_relation` VALUES ('105', '1', '105');
INSERT INTO `ums_role_menu_relation` VALUES ('106', '1', '106');

INSERT INTO `ums_role_menu_relation` VALUES ('200', '2', '200');
INSERT INTO `ums_role_menu_relation` VALUES ('201', '2', '201');
INSERT INTO `ums_role_menu_relation` VALUES ('202', '2', '202');
INSERT INTO `ums_role_menu_relation` VALUES ('203', '2', '203');

-- ----------------------------
-- Table structure for ums_role_resource_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_role_resource_relation`;
CREATE TABLE `ums_role_resource_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='后台角色资源关系表';

-- ----------------------------
-- Records of ums_role_resource_relation
-- ----------------------------
INSERT INTO `ums_role_resource_relation` VALUES ('1', '1', '1');
INSERT INTO `ums_role_resource_relation` VALUES ('2', '1', '2');
INSERT INTO `ums_role_resource_relation` VALUES ('3', '1', '3');
INSERT INTO `ums_role_resource_relation` VALUES ('4', '1', '4');
INSERT INTO `ums_role_resource_relation` VALUES ('5', '1', '5');
INSERT INTO `ums_role_resource_relation` VALUES ('6', '2', '1');
INSERT INTO `ums_role_resource_relation` VALUES ('7', '2', '2');
INSERT INTO `ums_role_resource_relation` VALUES ('8', '2', '3');
INSERT INTO `ums_role_resource_relation` VALUES ('9', '2', '4');
INSERT INTO `ums_role_resource_relation` VALUES ('10', '2', '5');
