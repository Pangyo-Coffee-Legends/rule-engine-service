package com.nhnacademy.ruleengineservice.service.schedule;

import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
class ComfortResultServiceTest {

    @Test
    @DisplayName("저장한 리스트 가 정확히 반환 되는지 확인")
    void updateAndGetLatestResults_ShouldStoreAndReturnResults() {
        ComfortResultService service = new ComfortResultService();

        RuleEvaluationResult result1 = new RuleEvaluationResult(1L, "rule1", true);
        RuleEvaluationResult result2 = new RuleEvaluationResult(2L, "rule2", false);

        List<RuleEvaluationResult> results = List.of(result1, result2);

        // 저장
        service.updateResults(results);

        // 가져 오기
        List<RuleEvaluationResult> latest = service.getLatestResults();

        assertEquals(results, latest);
    }

    @Test
    @DisplayName("값을 한 번도 저장 하지 않았을 때 [] 반환")
    void getLatestResults_ShouldReturnNull_WhenNotSet() {
        ComfortResultService service = new ComfortResultService();

        assertEquals(List.of(), service.getLatestResults());
    }
}