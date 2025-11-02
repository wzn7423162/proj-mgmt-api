-- 项目管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `proj_mgmt` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `proj_mgmt`;

-- 用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(30) NOT NULL COMMENT '用户账号',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `phone` varchar(11) DEFAULT '' COMMENT '手机号码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 插入默认管理员账号 (密码: admin123)
INSERT INTO `t_user` VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU4J6Vypi/6', '13800138000', '0', '0', NOW(), NOW());

-- 项目表
DROP TABLE IF EXISTS `t_project`;
CREATE TABLE `t_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(200) NOT NULL COMMENT '项目名称',
  `machine_count` int(11) DEFAULT 0 COMMENT '机台数量',
  `user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='项目信息表';

-- 机台表
DROP TABLE IF EXISTS `t_machine`;
CREATE TABLE `t_machine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '机台ID',
  `machine_name` varchar(200) NOT NULL COMMENT '机台名称',
  `project_id` bigint(20) NOT NULL COMMENT '所属项目ID',
  `import_time` datetime DEFAULT NULL COMMENT '导入机台的时间',
  `online_time` datetime DEFAULT NULL COMMENT '上线时间',
  `online_verified` tinyint(1) DEFAULT 0 COMMENT '上线验证（0-未验证，1-已验证）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_import_time` (`import_time`),
  KEY `idx_online_time` (`online_time`),
  CONSTRAINT `fk_machine_project` FOREIGN KEY (`project_id`) REFERENCES `t_project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机台信息表';

-- 插入测试数据
INSERT INTO `t_project` (`project_name`, `machine_count`, `user_id`) VALUES
('测试项目A', 0, 1),
('测试项目B', 0, 1);

INSERT INTO `t_machine` (`machine_name`, `project_id`, `import_time`, `online_time`, `online_verified`) VALUES
('机台-001', 1, NOW(), NULL, 0),
('机台-002', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 1),
('机台-003', 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 0);

-- 更新项目的机台数量
UPDATE `t_project` SET `machine_count` = (SELECT COUNT(*) FROM `t_machine` WHERE `project_id` = `t_project`.`id` AND `del_flag` = '0') WHERE `del_flag` = '0';

