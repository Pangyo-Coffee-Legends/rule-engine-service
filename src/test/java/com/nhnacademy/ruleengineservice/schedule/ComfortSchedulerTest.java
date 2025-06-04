package com.nhnacademy.ruleengineservice.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.dto.engine.RuleEvaluationResult;
import com.nhnacademy.ruleengineservice.service.engine.RuleEngineService;
import com.nhnacademy.ruleengineservice.service.schedule.ComfortResultService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ComfortSchedulerTest {
    @Mock
    private ComfortInfoBuffer buffer;

    @Mock
    private RuleEngineService ruleEngineService;

    @Mock
    private ComfortResultService comfortResultService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ComfortScheduler comfortScheduler;

    private final ComfortInfoDTO sampleInfo = new ComfortInfoDTO(
            "보드", 27.0, 65.0, 1300.0, "덥고 습함", "CO2 주의"
    );

    @Test
    void processComfortInfos_whenBufferEmpty_shouldDoNothing() {
        when(buffer.drainAll()).thenReturn(Collections.emptyList());

        comfortScheduler.processComfortInfos();

        verify(buffer).drainAll();
        verifyNoInteractions(ruleEngineService, comfortResultService);
    }

    @Test
    void processComfortInfos_whenBufferHasData_shouldProcessAndUpdateResults() {
        // Given
        List<ComfortInfoDTO> infos = List.of(sampleInfo);
        Map<String, Object> mockFacts = new HashMap<>(Map.of("temperature", 27.0));
        List<RuleEvaluationResult> mockResults = List.of(new RuleEvaluationResult());

        when(buffer.drainAll()).thenReturn(infos);
        when(objectMapper.convertValue(any(), (TypeReference<Object>) any()))  // 모든 인자 any()로 처리
                .thenReturn(mockFacts);
        when(ruleEngineService.executeTriggeredRules(any(), any(), any()))
                .thenReturn(mockResults);

        // When
        comfortScheduler.processComfortInfos();

        // Then
        verify(buffer).drainAll();

        // 타입만 검증 (인스턴스 무시)
        verify(objectMapper).convertValue(any(ComfortInfoDTO.class), any(TypeReference.class));

        // 룰 엔진 호출 검증
        verify(ruleEngineService).executeTriggeredRules(
                eq("AI_DATA_RECEIVED"),
                eq("{\"source\":\"AI\"}"),
                any(Map.class)
        );

        // 결과 업데이트 검증
        verify(comfortResultService).updateResults(mockResults);
    }
}