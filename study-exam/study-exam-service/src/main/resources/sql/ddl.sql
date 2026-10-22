-- study-exam schema | MySQL 8.0+
-- For new databases. Review/migrate existing study_exam before applying.
CREATE TABLE IF NOT EXISTS `study_exam_category` (
  `id` BIGINT NOT NULL COMMENT '分类ID，由应用生成',
  `category_code` VARCHAR(64) NOT NULL COMMENT '分类编码',
  `category_name` VARCHAR(128) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(500) DEFAULT NULL,
  `icon_url` VARCHAR(500) DEFAULT NULL,
  `sort` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0正常 1删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_category_visible` (`deleted`, `status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试分类';

CREATE TABLE IF NOT EXISTS `study_exam` (
  `id` BIGINT NOT NULL COMMENT '考试ID，由应用生成',
  `category_id` BIGINT NOT NULL COMMENT '分类ID，业务关联',
  `exam_code` VARCHAR(64) NOT NULL COMMENT '考试编码',
  `exam_name` VARCHAR(128) NOT NULL COMMENT '考试名称',
  `description` VARCHAR(500) DEFAULT NULL,
  `icon_url` VARCHAR(500) DEFAULT NULL,
  `sort` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_code` (`exam_code`),
  KEY `idx_exam_category` (`category_id`, `deleted`, `status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标准考试项目';

CREATE TABLE IF NOT EXISTS `study_exam_attribute` (
  `id` BIGINT NOT NULL COMMENT '属性配置ID，由应用生成',
  `exam_id` BIGINT NOT NULL COMMENT '标准考试ID',
  `attribute_code` VARCHAR(64) NOT NULL COMMENT '属性编码',
  `attribute_name` VARCHAR(128) NOT NULL COMMENT '属性名称',
  `input_type` VARCHAR(32) NOT NULL COMMENT 'text/select/number',
  `options_json` JSON DEFAULT NULL COMMENT 'select选项数组，如[{"label":"小学","value":"primary"}]',
  `required` TINYINT NOT NULL DEFAULT 0,
  `sort` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_attribute` (`exam_id`, `attribute_code`),
  KEY `idx_exam_attribute_list` (`exam_id`, `status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试动态属性配置';

CREATE TABLE IF NOT EXISTS `study_user_exam` (
  `id` BIGINT NOT NULL COMMENT '用户备考目标ID，由应用生成',
  `study_user_id` VARCHAR(32) NOT NULL COMMENT 'study-user业务用户ID',
  `exam_id` BIGINT DEFAULT NULL COMMENT '标准考试ID，未来自定义考试可为空',
  `exam_name` VARCHAR(128) NOT NULL COMMENT '考试名称快照',
  `exam_year` SMALLINT UNSIGNED DEFAULT NULL,
  `exam_date` DATE DEFAULT NULL,
  `attributes` JSON DEFAULT NULL COMMENT '用户填写的扩展属性',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1备考中 2已完成 3已取消',
  `is_current` TINYINT NOT NULL DEFAULT 1 COMMENT '1当前目标 0非当前目标',
  `source` VARCHAR(24) NOT NULL DEFAULT 'MANUAL' COMMENT 'INITIAL/MANUAL',
  `initial_user_key` VARCHAR(32) GENERATED ALWAYS AS (
    CASE WHEN `source` = 'INITIAL' THEN `study_user_id` ELSE NULL END
  ) STORED COMMENT '唯一限制每用户一条首次初始化记录',
  `current_user_key` VARCHAR(32) GENERATED ALWAYS AS (
    CASE WHEN `is_current` = 1 THEN `study_user_id` ELSE NULL END
  ) STORED COMMENT '唯一限制每用户一个当前目标',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_initial_user` (`initial_user_key`),
  UNIQUE KEY `uk_current_user` (`current_user_key`),
  KEY `idx_user_exams` (`study_user_id`, `create_time`),
  KEY `idx_user_exam` (`exam_id`),
  CONSTRAINT `chk_user_exam_current` CHECK (`is_current` IN (0,1)),
  CONSTRAINT `chk_user_exam_source` CHECK (`source` IN ('INITIAL','MANUAL'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户备考目标';
