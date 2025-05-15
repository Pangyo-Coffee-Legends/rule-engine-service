-- SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS conditions;
DROP TABLE IF EXISTS rule_member_mappings;
DROP TABLE IF EXISTS trigger_events;
DROP TABLE IF EXISTS rule_parameters;
DROP TABLE IF EXISTS rule_schedules;
DROP TABLE IF EXISTS actions;
DROP TABLE IF EXISTS rules;
DROP TABLE IF EXISTS sensors;
DROP TABLE IF EXISTS rule_groups;

SET REFERENTIAL_INTEGRITY TRUE;

DROP TABLE IF EXISTS conditions;
CREATE TABLE conditions (
                            con_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                            rule_no BIGINT NOT NULL,
                            con_type VARCHAR(50) NOT NULL,
                            con_field VARCHAR(50) NOT NULL,
                            con_value VARCHAR(100) NOT NULL,
                            con_priority INT NOT NULL,
                            created_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS rule_member_mappings;
CREATE TABLE rule_member_mappings (
                                      rule_mb_mapping_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      rule_no BIGINT NOT NULL,
                                      mb_no BIGINT NOT NULL
);

DROP TABLE IF EXISTS trigger_events;
CREATE TABLE trigger_events (
                                event_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                                rule_no BIGINT NOT NULL,
                                event_type VARCHAR(50) NOT NULL,
                                event_params TEXT NOT NULL,
                                created_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS rule_parameters;
CREATE TABLE rule_parameters (
                                 param_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 rule_no BIGINT NOT NULL,
                                 param_name VARCHAR(50) NOT NULL,
                                 param_value VARCHAR(100) NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP
);

DROP TABLE IF EXISTS rule_groups;
CREATE TABLE rule_groups (
                             rule_group_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                             rule_group_name VARCHAR(50) NOT NULL,
                             rule_group_description VARCHAR(200),
                             active BOOLEAN NOT NULL,
                             priority INT NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP
);

DROP TABLE IF EXISTS actions;
CREATE TABLE actions (
                         act_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                         rule_no BIGINT NOT NULL,
                         act_type VARCHAR(50) NOT NULL,
                         act_params TEXT NOT NULL,
                         act_priority INT NOT NULL,
                         created_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS rules;
CREATE TABLE rules (
                       rule_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                       rule_group_no BIGINT NOT NULL,
                       rule_name VARCHAR(50) NOT NULL,
                       rule_description VARCHAR(200),
                       rule_priority INT NOT NULL,
                       active BOOLEAN NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP
);

DROP TABLE IF EXISTS rule_schedules;
CREATE TABLE rule_schedules (
                                schedule_no BIGINT PRIMARY KEY AUTO_INCREMENT,
                                rule_no BIGINT NOT NULL,
                                cron_expression VARCHAR(50) NOT NULL,
                                time_zone VARCHAR(50) NOT NULL,
                                max_retires INT NOT NULL,
                                active BOOLEAN NOT NULL,
                                created_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS sensors;
CREATE TABLE sensors (
                         sensor_no BIGINT PRIMARY KEY,
                         sensor_name VARCHAR(50) NOT NULL,
                         sensor_type VARCHAR(50) NOT NULL,
                         sensor_state BOOLEAN NOT NULL,
                         location VARCHAR(100) NOT NULL
);
