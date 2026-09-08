CREATE TABLE IF NOT EXISTS `study_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'user id',
    `user_id` varchar(16) NOT NULL COMMENT '用户id',
    `username` varchar(64) NOT NULL COMMENT 'login username',
    `password` varchar(128) NOT NULL DEFAULT '' COMMENT 'login password',
    `mobile` varchar(11) DEFAULT NULL COMMENT 'mobile phone',
    `nickname` varchar(64) DEFAULT NULL COMMENT 'nickname',
    `email` varchar(128) DEFAULT NULL COMMENT 'email',
    `avatar_url` varchar(512) DEFAULT NULL COMMENT 'avatar url',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT 'user status',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_study_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='study user';

CREATE TABLE IF NOT EXISTS `study_wx_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'wx user id',
    `study_user_id` varchar(16) NOT NULL COMMENT 'study user id',
    `app_id` varchar(64) NOT NULL COMMENT 'wechat app id',
    `open_id` varchar(128) DEFAULT NULL COMMENT 'wechat open id',
    `union_id` varchar(128) DEFAULT NULL COMMENT 'wechat union id',
    `session_key` varchar(256) DEFAULT NULL COMMENT 'wechat session key',
    `mobile` varchar(11) DEFAULT NULL COMMENT 'mobile',
    `nickname` varchar(64) DEFAULT NULL COMMENT 'wechat nickname',
    `avatar_url` varchar(512) DEFAULT NULL COMMENT 'avatar url',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT 'wx user status',
    `last_login_at` datetime DEFAULT NULL COMMENT 'last login time',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updated time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wx_app_openid` (`app_id`, `open_id`),
    KEY `idx_wx_union_id` (`union_id`),
    KEY `idx_wx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='study wx user';
