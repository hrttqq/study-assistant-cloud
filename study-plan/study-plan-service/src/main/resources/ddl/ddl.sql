-- =============================================
-- Study Plan Module
-- MySQL 8.0+
-- =============================================

-- 1. 学习计划主表
CREATE TABLE `study_plan` (
                              `id` bigint NOT NULL COMMENT '学习计划ID，雪花ID',
                              `study_user_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
                              `user_exam_id` bigint NOT NULL COMMENT '关联 study_user_exam.id',
                              `exam_category_id` bigint NOT NULL COMMENT '关联考试分类id',
                              `plan_name` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
                              `plan_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CUSTOM' COMMENT '计划类型 CUSTOM/AI',
                              `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '计划描述',
                              `start_date` date NOT NULL COMMENT '计划开始日期',
                              `end_date` date NOT NULL COMMENT '计划结束日期',
                              `daily_study_minutes` int NOT NULL DEFAULT '60' COMMENT '每日目标学习分钟数',
                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '1进行中 2已完成 3已暂停 4已取消',
                              `source` varchar(24) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MANUAL' COMMENT 'MANUAL/AI',
                              `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '0正常 1删除',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_plan_user_exam` (`user_exam_id`),
                              KEY `idx_plan_user` (`study_user_id`,`status`,`deleted`),
                              KEY `idx_plan_date` (`start_date`,`end_date`),
                              CONSTRAINT `chk_plan_daily_minutes` CHECK ((`daily_study_minutes` > 0)),
                              CONSTRAINT `chk_plan_date` CHECK ((`end_date` >= `start_date`)),
                              CONSTRAINT `chk_plan_source` CHECK ((`source` in (_utf8mb4'MANUAL',_utf8mb4'AI'))),
                              CONSTRAINT `chk_plan_status` CHECK ((`status` in (1,2,3,4))),
                              CONSTRAINT `chk_plan_type` CHECK ((`plan_type` in (_utf8mb4'CUSTOM',_utf8mb4'AI')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习计划';


-- 2. 学习任务表
CREATE TABLE `study_plan_task` (
                                   `id` bigint NOT NULL COMMENT '任务ID，雪花ID',
                                   `plan_id` bigint NOT NULL COMMENT '学习计划ID',
                                   `study_user_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
                                   `task_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
                                   `task_content` text COLLATE utf8mb4_unicode_ci COMMENT '任务内容',
                                   `task_date` date NOT NULL COMMENT '计划执行日期',
                                   `estimated_minutes` int NOT NULL DEFAULT '30' COMMENT '预计学习分钟数',
                                   `actual_minutes` int NOT NULL DEFAULT '0' COMMENT '实际学习分钟数',
                                   `sort` int NOT NULL DEFAULT '0' COMMENT '任务排序',
                                   `status` tinyint NOT NULL DEFAULT '0' COMMENT '0待完成 1已完成 2已跳过',
                                   `completed_time` datetime DEFAULT NULL COMMENT '完成时间',
                                   `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '0正常 1删除',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_task_plan_date` (`plan_id`,`task_date`,`status`,`deleted`),
                                   KEY `idx_task_user_date` (`study_user_id`,`task_date`),
                                   CONSTRAINT `chk_task_minutes` CHECK (((`estimated_minutes` >= 0) and (`actual_minutes` >= 0))),
                                   CONSTRAINT `chk_task_status` CHECK ((`status` in (0,1,2)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习计划任务';


-- 3. 学习打卡记录表
CREATE TABLE `study_plan_checkin` (
                                      `id` bigint NOT NULL COMMENT '打卡记录ID，雪花ID',
                                      `plan_id` bigint NOT NULL COMMENT '学习计划ID',
                                      `task_id` bigint NOT NULL COMMENT '学习任务ID',
                                      `study_user_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
                                      `checkin_date` date NOT NULL COMMENT '打卡日期',
                                      `study_minutes` int NOT NULL DEFAULT '0' COMMENT '本次学习分钟数',
                                      `note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学习心得',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`),
                                      KEY `idx_checkin_user_date` (`study_user_id`,`checkin_date`),
                                      KEY `idx_checkin_task` (`task_id`,`checkin_date`),
                                      KEY `idx_checkin_plan` (`plan_id`,`checkin_date`),
                                      CONSTRAINT `chk_checkin_minutes` CHECK ((`study_minutes` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习打卡记录';
