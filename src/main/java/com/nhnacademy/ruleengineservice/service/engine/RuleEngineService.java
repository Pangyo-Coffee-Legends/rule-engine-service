package com.nhnacademy.ruleengineservice.service.engine;

import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResult;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.repository.trigger.TriggerRepository;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 규칙 엔진의 핵심 평가 및 실행 로직을 담당하는 서비스입니다.
 * - 입력 데이터(facts)와 룰셋을 받아 조건 평가, 액션 실행, 결과 반환을 수행합니다.
 * - 트리거, 스케줄, 외부 이벤트 등 다양한 상황에서 호출될 수 있습니다.
 */
@Slf4j
@Service
@Transactional
public class RuleEngineService {

    private final TriggerRepository triggerRepository;
    private final RuleService ruleService;
    private final ConditionService conditionService;
    private final ActionService actionService;

    public RuleEngineService(
            TriggerRepository triggerRepository,
            RuleService ruleService,
            ConditionService conditionService,
            ActionService actionService
    ) {
        this.triggerRepository = triggerRepository;
        this.ruleService = ruleService;
        this.conditionService = conditionService;
        this.actionService = actionService;
    }

    // facts 맵에서 필요한 데이터 검증
    private void validateRequiredFacts(Map<String, Object> facts, List<String> requiredFields) {
        for (String field : requiredFields) {
            if (!facts.containsKey(field)) {
                throw new IllegalArgumentException("필수 데이터 누락: " + field);
            }
        }
    }

    /**
     * 트리거 이벤트에 따른 룰 실행
     */
    public List<RuleEvaluationResult> executeTriggeredRules(String eventType, String eventParams, Map<String, Object> facts) {
        log.debug("RuleEngineService 실행 - eventType: {}, facts: {}", eventType, facts);
        // 트리거 이벤트로 실행 대상 룰 찾기
        List<TriggerEvent> triggerEvents = triggerRepository.findByEventType(eventType);
        List<Rule> rulesToEvaluate = triggerEvents.stream()
                .filter(trigger -> trigger.getEventParams() == null ||
                        trigger.getEventParams().isEmpty() ||
                        trigger.getEventParams().contains(eventParams))
                .map(TriggerEvent::getRule)
                .toList();

        // 룰별로 평가 및 액션 실행
        return rulesToEvaluate.stream()
                .map(rule -> evaluateAndExecuteRule(rule, facts))
                .toList();
    }

    /**
     * 단일 룰 평가 및 액션 실행
     */
    public RuleEvaluationResult evaluateAndExecuteRule(Rule rule, Map<String, Object> facts) {
        // 필수 필드 검증
        List<String> requiredFields = conditionService.getRequiredFieldsByRule(rule);
        validateRequiredFacts(facts, requiredFields);

        // 확장된 컨텍스트 생성
        Map<String, Object> enrichedContext = new HashMap<>(facts);
        enrichedContext.put("ruleNo", rule.getRuleNo());
        enrichedContext.put("ruleName", rule.getRuleName());
        enrichedContext.put("executionTime", LocalDateTime.now());

        // 조건 평가 및 결과 기록
        List<ConditionResult> conditionResults = conditionService.evaluateConditionsForRule(rule, facts);
        boolean allMatched = conditionResults.stream().allMatch(ConditionResult::isMatched);

        // 결과 객체 생성
        RuleEvaluationResult result = new RuleEvaluationResult(
                rule.getRuleNo(),
                rule.getRuleName(),
                allMatched
        );
        result.setConditionResults(conditionResults);

        // 조건 충족 시 액션 실행
        if (allMatched) {
            List<ActionResult> actionResults = actionService.executeActionsForRule(rule, enrichedContext);
            result.setExecutedActions(actionResults);
            result.setMessage("룰 조건 충족, 액션 실행 완료");
        } else {
            result.setMessage("룰 조건 불충족, 액션 미실행");
        }

        result.setEvaluatedAt(java.time.LocalDateTime.now());
        return result;
    }

    /**
     * 특정 룰을 수동으로 평가 및 실행
     */
    public RuleEvaluationResult executeRule(Long ruleNo, Map<String, Object> facts) {
        Rule rule = ruleService.getRuleEntity(ruleNo);
        return evaluateAndExecuteRule(rule, facts);
    }
}