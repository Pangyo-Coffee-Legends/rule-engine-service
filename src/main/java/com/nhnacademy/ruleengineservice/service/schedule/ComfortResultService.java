package com.nhnacademy.ruleengineservice.service.schedule;

import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ComfortResultService {
    private final AtomicReference<List<RuleEvaluationResult>> latestResults = new AtomicReference<>();

    public List<RuleEvaluationResult> getLatestResults() {
        return latestResults.get();
    }

    public void updateResults(List<RuleEvaluationResult> results) {
        latestResults.set(results);
    }
}
