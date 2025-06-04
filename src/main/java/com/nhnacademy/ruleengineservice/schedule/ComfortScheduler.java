package com.nhnacademy.ruleengineservice.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@code ComfortScheduler}는 IoT 장치에서 수집된 편의 정보({@link ComfortInfoDTO})를 주기적으로 처리하는 스케줄러 컴포넌트입니다.
 * <p>
 * Spring Boot 기반의 IoT 시스템에서 30초 간격으로 데이터를 처리하며, 다음 기능을 수행합니다:
 * <ul>
 *   <li>{@link ComfortInfoBuffer}에서 데이터를 일괄 추출</li>
 *   <li>룰 엔진({@link RuleEngineService})을 통해 AI 데이터 트리거 실행</li>
 *   <li>룰 실행 결과({@link RuleEvaluationResult})를 {@link ComfortResultService}에 저장</li>
 * </ul>
 * Spring Cloud 환경에서 동작하며, 예외 발생 시 자동 복구 메커니즘을 제공합니다.
 * </p>
 *
 * @author 강승우
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComfortScheduler {
    private final ComfortInfoBuffer buffer;
    private final RuleEngineService ruleEngineService;
    private final ComfortResultService comfortResultService;

    private final ObjectMapper objectMapper;

    /**
     * 30초 간격으로 편의 정보를 처리하는 스케줄링 작업 메서드입니다.
     * <p>
     * 주요 처리 단계:
     * <ol>
     *   <li>버퍼에서 모든 {@link ComfortInfoDTO} 추출</li>
     *   <li>각 정보를 팩트 맵으로 변환 후 룰 엔진 실행</li>
     *   <li>실행 결과 누적 및 최종 결과 저장</li>
     *   <li>예외 발생 시 데이터 재처리 준비</li>
     * </ol>
     * Postman 등 API 테스트 도구와 연동해 동작을 검증할 수 있습니다.
     * </p>
     */
    @Scheduled(fixedDelay = 30000) // 30초 간격으로 데이터 전송
    public void processComfortInfos() {
        List<ComfortInfoDTO> infos = buffer.drainAll();
        List<RuleEvaluationResult> allResults = new ArrayList<>();
        List<ComfortInfoDTO> tempInfos = new ArrayList<>(infos); // 복사본 생성
        log.info("Processing {} comfort infos", infos.size());

        for (ComfortInfoDTO info : tempInfos) {
            try {
                Map<String, Object> facts = objectMapper.convertValue(info, new TypeReference<>() {});
                facts.put("comfortInfo", info);

                List<RuleEvaluationResult> results = ruleEngineService.executeTriggeredRules(
                        "AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts);

                log.info("룰 엔진 실행결과 : {}개", results.size());
                allResults.addAll(results);
            } catch (Exception e) {
                log.error("룰 엔진 실행 중 오류 발생. info: {}", info, e);
                // 필요하다면 실패한 info를 별도 저장/재처리
                buffer.add(info);
            }
        }

        if (!allResults.isEmpty()) {
            comfortResultService.updateResults(allResults);
            log.info("총 {}개의 룰 평가 결과 저장 완료", allResults.size());
        } else {
            log.info("처리된 데이터가 없습니다.");
        }
    }
}
