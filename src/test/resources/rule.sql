SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

DROP TABLE IF EXISTS `conditions`;
CREATE TABLE `conditions` (
                              `con_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                              `rule_no` BIGINT NOT NULL COMMENT 'auto increment',
                              `con_type` VARCHAR(50) NOT NULL COMMENT '예시: EQ,GT,LT,IN,LIKE',
                              `con_field` VARCHAR(50) NOT NULL COMMENT '실제 데이터',
                              `con_value` VARCHAR(100) NOT NULL,
                              `con_priority` INT NOT NULL,
                              `created_at` DATETIME NOT NULL,
                              PRIMARY KEY (`con_no`)
);

DROP TABLE IF EXISTS `rule_member_mappings`;
CREATE TABLE `rule_member_mappings` (
                                        `rule_member_mapping_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                                        `rule_no` BIGINT NOT NULL,
                                        `mb_no` BIGINT NOT NULL,
                                        PRIMARY KEY (`rule_member_mapping_no`)
);

DROP TABLE IF EXISTS `trigger_events`;
CREATE TABLE `trigger_events` (
                                  `event_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                                  `rule_no` BIGINT NOT NULL COMMENT 'auto increment',
                                  `event_type` VARCHAR(50) NOT NULL COMMENT '예시: insert,schedule',
                                  `event_params` TEXT NOT NULL COMMENT 'JSON 형식',
                                  `created_at` DATETIME NOT NULL,
                                  PRIMARY KEY (`event_no`)
);

DROP TABLE IF EXISTS `rule_parameters`;
CREATE TABLE `rule_parameters` (
                                   `param_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                                   `rule_no` BIGINT NOT NULL COMMENT 'auto increment',
                                   `param_name` VARCHAR(50) NOT NULL,
                                   `param_value` VARCHAR(100) NOT NULL,
                                   `created_at` DATETIME NOT NULL,
                                   `updated_at` DATETIME NULL,
                                   PRIMARY KEY (`param_no`)
);

DROP TABLE IF EXISTS `rule_groups`;
CREATE TABLE `rule_groups` (
                               `rule_group_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                               `rule_group_name` VARCHAR(50) NOT NULL,
                               `rule_group_description` VARCHAR(200) NULL,
                               `active` BOOLEAN NOT NULL,
                               `priority` INT NOT NULL,
                               `created_at` DATETIME NOT NULL,
                               `updated_at` DATETIME NULL,
                               PRIMARY KEY (`rule_group_no`)
);

DROP TABLE IF EXISTS `actions`;
CREATE TABLE `actions` (
                           `act_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                           `rule_no` BIGINT NOT NULL COMMENT 'auto increment',
                           `act_type` VARCHAR(50) NOT NULL COMMENT '예시: EMAIL,PUSH',
                           `act_params` TEXT NOT NULL COMMENT 'JSON 형식',
                           `act_priority` INT NOT NULL,
                           `created_at` DATETIME NOT NULL,
                           PRIMARY KEY (`act_no`)
);

DROP TABLE IF EXISTS `rules`;
CREATE TABLE `rules` (
                         `rule_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                         `rule_group_no` BIGINT NOT NULL,
                         `rule_name` VARCHAR(50) NOT NULL,
                         `rule_description` VARCHAR(200) NULL,
                         `rule_priority` INT NOT NULL,
                         PRIMARY KEY (`rule_no`)
);

DROP TABLE IF EXISTS `rule_schedules`;
CREATE TABLE `rule_schedules` (
                                  `schedule_no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment',
                                  `rule_no` BIGINT NOT NULL COMMENT 'auto increment',
                                  `cron_expression` VARCHAR(50) NOT NULL COMMENT 'scheduleRecurrently() 파라미터',
                                  `time_zone` VARCHAR(50) NOT NULL COMMENT '예시: Asia/Seoul',
                                  `max_retires` INT NOT NULL COMMENT '무한반복 방지, 실패시 자동 복구',
                                  `active` BOOLEAN NOT NULL COMMENT 'schedule/delete API 호출로 부여',
                                  `created_at` DATETIME NOT NULL,
                                  PRIMARY KEY (`schedule_no`)
);

DROP TABLE IF EXISTS `sensors`;
CREATE TABLE `sensors` (
                           `sensor_no` BIGINT NOT NULL,
                           `sensor_name` VARCHAR(50) NOT NULL,
                           `sensor_type` VARCHAR(50) NOT NULL,
                           `sensor_state` BOOLEAN NOT NULL,
                           `location` VARCHAR(100) NOT NULL,
                           PRIMARY KEY (`sensor_no`)
);

