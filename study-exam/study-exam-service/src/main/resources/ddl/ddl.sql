CREATE TABLE `study_exam`
(
    `id` BIGINT NOT NULL COMMENT '考试ID',

    `exam_code` VARCHAR(64) NOT NULL COMMENT '考试编码',

    `exam_name` VARCHAR(128) NOT NULL COMMENT '考试名称',

    `category_code` VARCHAR(64) DEFAULT NULL COMMENT '考试分类编码',

    `category_name` VARCHAR(128) DEFAULT NULL COMMENT '考试分类名称',

    `description` VARCHAR(500) DEFAULT NULL COMMENT '考试简介',

    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '考试图标',

    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',

    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',

    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',

    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),

    UNIQUE KEY `uk_exam_code` (`exam_code`),

    KEY `idx_category_code` (`category_code`),

    KEY `idx_status_sort` (`status`, `sort`)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='考试项目表';