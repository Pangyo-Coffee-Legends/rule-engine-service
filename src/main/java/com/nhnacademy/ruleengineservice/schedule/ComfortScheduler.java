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

@Slf4j
@Component
@RequiredArgsConstructor
public class ComfortScheduler {
    private final ComfortInfoBuffer buffer;
    private final RuleEngineService ruleEngineService;
    private final ComfortResultService comfortResultService;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 30000) // 30초 간격으로 데이터 전송
    public void processComfortInfos() {
        List<ComfortInfoDTO> infos = buffer.drainAll();
        List<RuleEvaluationResult> allResults = new ArrayList<>();

        for (ComfortInfoDTO info : infos) {
            Map<String, Object> facts = objectMapper.convertValue(info, new TypeReference<>() {});
            log.info("Processing {} comfort infos", infos.size());

            facts.put("comfortInfo", info);

            List<RuleEvaluationResult> results = ruleEngineService.executeTriggeredRules(
                    "AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts);

            log.info("룰 엔진 실행결과 : {}개", results.size());

            allResults.addAll(results);
        }

        comfortResultService.updateResults(allResults);
    }
}
