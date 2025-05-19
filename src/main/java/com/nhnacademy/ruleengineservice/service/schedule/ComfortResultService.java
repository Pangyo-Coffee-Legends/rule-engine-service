package com.nhnacademy.ruleengineservice.service.schedule;

import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ComfortResultService {
    private List<RuleEvaluationResult> latestResults = new ArrayList<>();

    public List<RuleEvaluationResult> getLatestResults() {
        return latestResults;
    }

    public void updateResults(List<RuleEvaluationResult> results) {
        if (!results.isEmpty()) {
            log.info("결과 저장 : {}개", results.size());
            latestResults = results;
        }
    }
}
