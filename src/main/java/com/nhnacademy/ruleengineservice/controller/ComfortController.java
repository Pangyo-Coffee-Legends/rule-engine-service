package com.nhnacademy.ruleengineservice.controller;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.schedule.ComfortInfoBuffer;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * {@code ComfortController}는 쾌적도(Comfort) 관련 IoT 데이터를 수신 및 조회하는 REST API 컨트롤러입니다.
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>{@link #receiveComfortInfo(ComfortInfoDTO)}: 쾌적도 센서 데이터(ComfortInfoDTO)를 POST 방식으로 수신하여 버퍼에 저장합니다.</li>
 *   <li>{@link #getScheduledResult()}: 스케줄러에 의해 집계된 최신 룰 평가 결과 목록을 GET 방식으로 제공합니다.</li>
 * </ul>
 * <p>
 * Spring Boot 기반 IoT 서비스에서 센서 데이터 수집, 버퍼링, 결과 조회를 담당하며,
 * 실시간 데이터 처리와 API 통합 환경(Spring Cloud, Feign 등)에서 활용됩니다
 * </p>
 *
 * @author 강승우
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/comfort")
@RequiredArgsConstructor
public class ComfortController {
    private final ComfortInfoBuffer buffer;
    private final ComfortResultService comfortResultService;

    /**
     * 쾌적도 센서 데이터를 수신하여 버퍼에 저장합니다.
     *
     * @param comfortInfo 수신한 쾌적도 데이터
     * @return 성공 응답(HTTP 200)
     */
    @PostMapping
    public ResponseEntity<Void> receiveComfortInfo(@RequestBody ComfortInfoDTO comfortInfo) {
        log.debug("받은 정보: {}", comfortInfo);
        buffer.add(comfortInfo);
        return ResponseEntity.ok().build();
    }

    /**
     * 스케줄러에 의해 집계된 최신 룰 평가 결과를 조회합니다.
     *
     * @return 최신 룰 평가 결과 리스트(HTTP 200)
     */
    @GetMapping("/scheduled-result")
    public ResponseEntity<List<RuleEvaluationResult>> getScheduledResult() {
        List<RuleEvaluationResult> results = comfortResultService.getLatestResults();

        log.info("GET /scheduled-result - 결과 개수 : {}개", results.size());

        return ResponseEntity.ok(results);
    }
}