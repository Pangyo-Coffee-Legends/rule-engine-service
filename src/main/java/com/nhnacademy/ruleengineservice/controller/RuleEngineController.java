package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 규칙 엔진(Rule Engine) 관련 API를 제공하는 REST 컨트롤러입니다.
 * <p>
 * 이 컨트롤러는 트리거 이벤트 기반의 룰 평가 및 실행,
 * 수동 룰 평가 및 실행 등 룰 엔진의 핵심 기능을 HTTP 엔드포인트로 제공합니다.
 * </p>
 * <p>
 * 주요 역할:
 * <ul>
 *   <li>트리거 이벤트 발생 시 룰 평가 및 액션 실행 (POST /api/v1/rule-engine/trigger)</li>
 *   <li>특정 룰을 수동으로 평가 및 실행 (POST /api/v1/rule-engine/manual/{ruleNo})</li>
 *   <li>룰 평가 결과 및 액션 실행 결과를 클라이언트에 반환</li>
 * </ul>
 * </p>
 * <p>
 * 주요 연동 대상:
 * <ul>
 *   <li>AI LSTM, IoT, Data-Processesor 등 다양한 시스템과의 통합</li>
 *   <li>MySQL 기반 rules, conditions, actions, trigger_events 등 테이블</li>
 * </ul>
 * </p>
 *
 * @author 강승우
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/rule-engine")
public class RuleEngineController {

    private final RuleEngineService ruleEngineService;

    public RuleEngineController(RuleEngineService ruleEngineService) {
        this.ruleEngineService = ruleEngineService;
    }

    /**
     * 트리거 이벤트가 발생했을 때 룰 평가 및 액션 실행을 수행합니다.
     *
     * @param eventType   트리거 이벤트 타입 (예: "AI_DATA_RECEIVED", "API_CALL" 등)
     * @param eventParams 트리거 이벤트 파라미터 (예: JSON 문자열)
     * @param facts       룰 평가에 사용할 입력 데이터 (예: location, comfort 등)
     * @return 룰 평가 및 액션 실행 결과 리스트
     */
    @PostMapping("/trigger")
    public ResponseEntity<List<RuleEvaluationResult>> executeTriggeredRules(
            @RequestParam String eventType,
            @RequestParam(required = false, defaultValue = "{}") String eventParams,
            @RequestBody Map<String, Object> facts
    ) {
        List<RuleEvaluationResult> results = ruleEngineService.executeTriggeredRules(eventType, eventParams, facts);

        log.debug("executeTriggeredRules : {}", results);

        return ResponseEntity
                .ok(results);
    }

    /**
     * 특정 룰을 수동으로 평가 및 실행합니다.
     *
     * @param ruleNo 룰 식별자
     * @param facts  룰 평가에 사용할 입력 데이터
     * @return 룰 평가 및 액션 실행 결과
     */
    @PostMapping("/manual/{ruleNo}")
    public ResponseEntity<RuleEvaluationResult> executeRule(
            @PathVariable Long ruleNo,
            @RequestBody Map<String, Object> facts
    ) {
        RuleEvaluationResult result = ruleEngineService.executeRule(ruleNo, facts);

        log.debug("executeRule : {}", result);

        return ResponseEntity
                .ok(result);
    }
}
