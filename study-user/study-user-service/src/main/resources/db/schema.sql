CREATE TABLE `study_user` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，系统内部唯一标识',
                              `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名\r\n用户名，系统登录账号或自动生成账号',
                              `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码，加密存储；微信纯登录用户可为空',
                              `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号码',
                              `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户昵称',
                              `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱地址',
                              `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像地址',
                              `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态：1正常 0禁用',
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户主表';


CREATE TABLE `study_wx_user` (
                                 `id` bigint NOT NULL COMMENT '微信用户id',
                                 `study_user_id` bigint NOT NULL COMMENT '考试助手用户id',
                                 `app_id` varchar(64) NOT NULL COMMENT '应用id',
                                 `open_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信openId',
                                 `union_id` varchar(128) DEFAULT NULL,
                                 `session_key` varchar(256) DEFAULT NULL,
                                 `mobile` varchar(11) DEFAULT NULL COMMENT '手机号码',
                                 `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信昵称',
                                 `avatar_url` varchar(512) DEFAULT NULL COMMENT '头像地址',
                                 `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '微信用户状态：1正常 0解绑 2禁用',
                                 `last_login_at` datetime DEFAULT NULL,
                                 `created_at` datetime NOT NULL COMMENT '创建时间',
                                 `updated_at` datetime NOT NULL COMMENT '修改时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_wx_app_openid` (`app_id`,`open_id`),
                                 KEY `idx_wx_union_id` (`union_id`),
                                 KEY `idx_wx_user_id` (`study_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;