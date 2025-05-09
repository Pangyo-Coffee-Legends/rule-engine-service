package com.nhnacademy.ruleengineservice.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ComfortScheduler {
    private final ComfortInfoBuffer buffer;
    private final RuleEngineService ruleEngineService;
    private final ComfortResultService comfortResultService;

    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 * * * * *")
    public void processComfortInfos() {
        List<ComfortInfoDTO> infos = buffer.drainAll();
        List<RuleEvaluationResult> allResults = new ArrayList<>();

        for (ComfortInfoDTO info : infos) {
            Map<String, Object> facts = objectMapper.convertValue(info, new TypeReference<>() {});

            List<RuleEvaluationResult> results = ruleEngineService.executeTriggeredRules(
                    "AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts);
            allResults.addAll(results);
        }

        comfortResultService.updateResults(allResults);
    }
}
