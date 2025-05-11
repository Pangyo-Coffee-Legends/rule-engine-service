INSERT INTO RULE_GROUPS (rule_group_name, rule_group_description, active, priority, created_at)
VALUES
    ('1층', '1층 룰 그룹', true, 1, NOW()),
    ('2층', '2층 룰 그룹', true, 2, NOW());

INSERT INTO RULES (rule_group_no, rule_name, rule_description, rule_priority, active, created_at, updated_at)
VALUES
    (1, '고온 경고', '온도가 30도 이상일 때 경고', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, '저온 경고', '온도가 20도 이하일 때 경고', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, '고습 경고', '습도가 70% 이상일 때 경고', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'CO2 경고', 'CO2가 1000ppm 이상일 때 경고', 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'CO2 경고', 'CO2가 1000ppm 이상일 때 경고', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO CONDITIONS (rule_no, con_type, con_field, con_value, con_priority, created_at)
VALUES
    (1, 'GT', 'temperature', '30', 1, NOW()),
    (2, 'LT', 'temperature', '20', 1, NOW()),
    (3, 'GT', 'humidity', '70', 1, NOW()),
    (4, 'GT', 'co2', '1000', 1, NOW()),
    (5, 'GT', 'co2', '1000', 1, NOW());

INSERT INTO ACTIONS (rule_no, act_type, act_params, act_priority, created_at)
VALUES
    (1, 'EMAIL', '{"to":"admin@example.com","subject":"고온 경고"}', 1, NOW()),
    (2, 'COMFORT_NOTIFICATION', '{"message":"습도가 높습니다!"}', 1, NOW()),
    (3, 'EMAIL', '{"to":"admin@example.com","subject":"CO2 경고"}', 1, NOW());

INSERT INTO RULE_SCHEDULES (rule_no, cron_expression, time_zone, max_retires, active, created_at)
VALUES
    (1, '0 0 * * *', 'Asia/Seoul', 3, true, NOW());

INSERT INTO RULE_PARAMETERS (rule_no, param_name, param_value, created_at)
VALUES
    (1, 'threshold', '30', NOW());

INSERT INTO RULE_MEMBER_MAPPINGS (rule_no, mb_no)
VALUES
    (1, 101),
    (2, 102);

INSERT INTO TRIGGER_EVENTS (rule_no, event_type, event_params, created_at)
VALUES
    (1, 'AI_DATA_RECEIVED', '{"source":"AI"}', NOW()),
    (2, 'AI_DATA_RECEIVED', '{"source":"AI"}', NOW()),
    (3, 'AI_DATA_RECEIVED', '{"source":"AI"}', NOW()),
    (4, 'AI_DATA_RECEIVED', '{"source":"AI"}', NOW()),
    (5, 'AI_DATA_RECEIVED', '{"source":"AI"}', NOW());

INSERT INTO SENSORS (sensor_no, sensor_name, sensor_type, sensor_state, location)
VALUES
    (1, '온도센서1', 'temperature', true, '회의실A'),
    (2, '습도센서1', 'humidity', true, '회의실A'),
    (3, 'CO2센서1', 'co2', true, '회의실A'),
    (4, 'CO2센서1', 'co2', true, '회의실B');
