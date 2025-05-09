package com.nhnacademy.ruleengineservice.schedule;

import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class ComfortSchedulerTest {
    private ComfortInfoBuffer buffer;
    private RuleEngineService ruleEngineService;
    private ComfortResultService comfortResultService;
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private ComfortScheduler scheduler;

    @BeforeEach
    void setUp() {
        buffer = mock(ComfortInfoBuffer.class);
        ruleEngineService = mock(RuleEngineService.class);
        comfortResultService = mock(ComfortResultService.class);
        objectMapper = mock(com.fasterxml.jackson.databind.ObjectMapper.class);

        scheduler = new ComfortScheduler(buffer, ruleEngineService, comfortResultService, objectMapper);
    }

    @Test
    @DisplayName("스케줄러 자동 시작 동작 확인")
    void processComfortInfos_shouldDrainBufferConvertAndUpdateResults() {
        // 준비: 버퍼에서 ComfortInfoDTO 2개 반환
        ComfortInfoDTO dto1 = mock(ComfortInfoDTO.class);
        ComfortInfoDTO dto2 = mock(ComfortInfoDTO.class);
        when(buffer.drainAll()).thenReturn(List.of(dto1, dto2));

        // 준비: ObjectMapper가 각각 Map으로 변환
        Map<String, Object> facts1 = Map.of("key1", "value1");
        Map<String, Object> facts2 = Map.of("key2", "value2");

        when(objectMapper.convertValue(eq(dto1), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn(facts1);
        when(objectMapper.convertValue(eq(dto2), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn(facts2);

        // 준비: RuleEngineService가 각각 결과 반환
        RuleEvaluationResult resultA = mock(RuleEvaluationResult.class);
        RuleEvaluationResult resultB = mock(RuleEvaluationResult.class);
        when(ruleEngineService.executeTriggeredRules("AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts1)).thenReturn(List.of(resultA));
        when(ruleEngineService.executeTriggeredRules("AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts2)).thenReturn(List.of(resultB));

        // 실행
        scheduler.processComfortInfos();

        // 검증: buffer.drainAll() 호출
        verify(buffer, times(1)).drainAll();

        // 검증: objectMapper.convertValue 각각 호출
        verify(objectMapper).convertValue(eq(dto1), any(com.fasterxml.jackson.core.type.TypeReference.class));
        verify(objectMapper).convertValue(eq(dto2), any(com.fasterxml.jackson.core.type.TypeReference.class));

        // 검증: ruleEngineService.executeTriggeredRules 각각 호출
        verify(ruleEngineService, times(1)).executeTriggeredRules("AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts1);
        verify(ruleEngineService, times(1)).executeTriggeredRules("AI_DATA_RECEIVED", "{\"source\":\"AI\"}", facts2);

        // 검증: comfortResultService.updateResults에 결과가 합쳐져 전달
        ArgumentCaptor<List<RuleEvaluationResult>> captor = ArgumentCaptor.forClass(List.class);
        verify(comfortResultService, times(1)).updateResults(captor.capture());
        List<RuleEvaluationResult> allResults = captor.getValue();
        assertEquals(List.of(resultA, resultB), allResults);
    }
}