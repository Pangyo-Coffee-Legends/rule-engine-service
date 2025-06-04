package com.nhnacademy.ruleengineservice.service.schedule;

import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code ComfortResultService}는 룰 평가 결과({@link RuleEvaluationResult})의 최신 상태를 관리하는 서비스 클래스입니다.
 * <p>
 * 최신 룰 평가 결과 목록을 저장하고, 외부에서 조회 및 갱신할 수 있도록 제공합니다.
 * 결과가 갱신될 때마다 저장된 결과의 개수를 로그로 기록합니다.
 * </p>
 *
 * <ul>
 *   <li>{@link #getLatestResults()}: 최신 룰 평가 결과 목록을 반환합니다.</li>
 *   <li>{@link #updateResults(List)}: 새로운 평가 결과 목록으로 갱신합니다. 비어있지 않은 경우에만 갱신됩니다.</li>
 * </ul>
 *
 * Spring의 {@code @Service} 및 {@code @Slf4j} 어노테이션이 적용되어 있습니다.
 *
 * @author 강승우
 * @since 1.0
 */
@Slf4j
@Service
public class ComfortResultService {
    private List<RuleEvaluationResult> latestResults = new ArrayList<>();

    /**
     * 최신 룰 평가 결과 목록을 반환합니다.
     *
     * @return 최신 {@link RuleEvaluationResult} 리스트
     */
    public List<RuleEvaluationResult> getLatestResults() {
        return latestResults;
    }

    /**
     * 최신 룰 평가 결과 목록을 갱신합니다.
     * 전달받은 결과 리스트가 비어있지 않을 때만 갱신하며,
     * 저장된 결과의 개수를 로그로 기록합니다.
     *
     * @param results 갱신할 {@link RuleEvaluationResult} 리스트
     */
    public void updateResults(List<RuleEvaluationResult> results) {
        if (!results.isEmpty()) {
            log.info("결과 저장 : {}개", results.size());
            latestResults = results;
        }
    }
}
