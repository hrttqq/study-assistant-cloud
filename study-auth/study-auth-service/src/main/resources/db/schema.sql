CREATE TABLE IF NOT EXISTS `study_permission` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
                                    `code` varchar(128) NOT NULL COMMENT '权限编码，如 ai:chat、plan:create、file:upload',
                                    `name` varchar(64) NOT NULL COMMENT '权限名称',
                                    `resource` varchar(64) NOT NULL COMMENT '资源名称，如 ai、plan、file、member',
                                    `action` varchar(64) NOT NULL COMMENT '操作名称，如 read、create、update、delete、chat、upload',
                                    `description` varchar(255) DEFAULT NULL COMMENT '权限说明',
                                    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

CREATE TABLE IF NOT EXISTS `study_role` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                              `code` varchar(64) NOT NULL COMMENT '角色编码，如 USER、ADMIN、MEMBER_PRO',
                              `name` varchar(64) NOT NULL COMMENT '角色名称',
                              `description` varchar(255) DEFAULT NULL COMMENT '角色说明',
                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '角色状态：1启用，0停用',
                              `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

CREATE TABLE IF NOT EXISTS `study_role_permission` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色权限关系ID',
                                         `role_code` varchar(64) NOT NULL COMMENT '角色编码，对应 study_role.code',
                                         `permission_code` varchar(128) NOT NULL COMMENT '权限编码，对应 study_permission.code',
                                         `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_role_permission` (`role_code`,`permission_code`),
                                         KEY `idx_role_permission_role_code` (`role_code`),
                                         KEY `idx_role_permission_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关系表';

CREATE TABLE IF NOT EXISTS `study_user_role` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户角色关系ID',
                                   `user_id` bigint NOT NULL COMMENT '用户ID，对应 study_user.id',
                                   `role_code` varchar(64) NOT NULL COMMENT '角色编码，对应 study_role.code',
                                   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_user_role` (`user_id`,`role_code`),
                                   KEY `idx_user_role_user_id` (`user_id`),
                                   KEY `idx_user_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关系表';

CREATE TABLE IF NOT EXISTS `study_login_log` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '登录日志ID',
                                   `user_id` bigint DEFAULT NULL COMMENT '用户ID，登录失败或未绑定时可为空',
                                   `login_type` varchar(32) NOT NULL COMMENT '登录类型：WX微信，PASSWORD账号密码，SMS短信',
                                   `login_account` varchar(128) DEFAULT NULL COMMENT '登录账号标识，如用户名、手机号、open_id',
                                   `ip` varchar(64) DEFAULT NULL COMMENT '登录IP',
                                   `user_agent` varchar(512) DEFAULT NULL COMMENT '客户端User-Agent',
                                   `success` tinyint NOT NULL COMMENT '是否登录成功：1成功，0失败',
                                   `fail_reason` varchar(255) DEFAULT NULL COMMENT '登录失败原因',
                                   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_login_log_user_id` (`user_id`),
                                   KEY `idx_login_log_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志表';