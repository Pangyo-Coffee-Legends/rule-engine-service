package com.nhnacademy.ruleengineservice.service.engine.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import com.nhnacademy.ruleengineservice.service.engine.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@code ExecutionServiceImpl}는 {@link ExecutionService}의 구현체로,
 * 룰(rule)에 정의된 액션(action)들을 실행하고 그 결과를 반환하는 서비스 클래스입니다.
 * <p>
 * 각 {@link Rule}의 액션 리스트를 순회하며 {@link ActionService}를 통해 실제 액션을 수행하고,
 * 실행 결과({@link ActionResult})를 수집합니다.
 * </p>
 *
 * <ul>
 *   <li>{@link #executeActions(Rule, Map)}: 주어진 룰과 컨텍스트 정보로 모든 액션을 실행하고 결과 리스트를 반환합니다.</li>
 *   <li>{@link #executeAction(Action, Map)}: 단일 액션을 실행하고 결과를 반환합니다.</li>
 * </ul>
 *
 * Spring의 {@code @Service} 및 {@code @Transactional} 어노테이션이 적용되어 있으며,
 * 내부적으로 {@code @Slf4j}를 사용해 실행 로그를 기록합니다.
 *
 * @author 강승우
 * @since 1.0
 */
@Slf4j
@Service
@Transactional
public class ExecutionServiceImpl implements ExecutionService {

    private final ActionService actionService;

    public ExecutionServiceImpl(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public List<ActionResult> executeActions(Rule rule, Map<String, Object> facts) {
        List<ActionResult> results = new ArrayList<>();

        for (Action action : rule.getActionList()) {
            results.add(executeAction(action, facts));
        }

        log.debug("executeActions : {}", results);

        return results;
    }

    @Override
    public ActionResult executeAction(Action action, Map<String, Object> context) {
        log.debug("executeAction success");
        return actionService.performAction(action.getActNo(), context);
    }
}
