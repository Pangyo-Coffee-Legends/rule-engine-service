package com.nhnacademy.ruleengineservice.service.parameter.impl;

import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.parameter.ParameterRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.parameter.ParameterResponse;
import com.nhnacademy.ruleengineservice.exception.parameter.ParameterNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.parameter.RuleParameterRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import com.nhnacademy.ruleengineservice.service.rule.impl.RuleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class ParameterServiceImplTest {

    @Mock
    private RuleParameterRepository ruleParameterRepository;

    @Mock
    private RuleServiceImpl ruleService;

    @InjectMocks
    private ParameterServiceImpl parameterService;

    @Test
    @DisplayName("파라미터 등록 성공")
    void registerParameter() {
        Long ruleNo = 1L;
        String paramName = "testParam";
        String paramValue = "testValue";

        ParameterRegisterRequest request = new ParameterRegisterRequest(ruleNo, paramName, paramValue);

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("group10", "description10", 10);
        Rule mockRule = Rule.ofNewRule(ruleGroup, "rule100", "description100", 100);
        setField(mockRule, "ruleNo", ruleNo);

        RuleParameter savedParameter = RuleParameter.ofNewRuleParameter(mockRule, paramName, paramValue);
        setField(savedParameter, "paramNo", 1L);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(ruleParameterRepository.save(Mockito.any())).thenReturn(savedParameter);

        ParameterResponse response = parameterService.registerParameter(request);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(1L, response.getParamNo()),
                () -> assertEquals(ruleNo, response.getRuleNo()),
                () -> assertEquals(paramName, response.getParamName()),
                () -> assertEquals(paramValue, response.getParamValue())
        );

        verify(ruleService).getRuleEntity(ruleNo);
        verify(ruleParameterRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("존재하지 않는 룰로 파라미터 등록 시 예외 발생")
    void registerParameter_withNonExistentRule_throwsException() {
        Long nonExistentRuleNo = 999L;
        ParameterRegisterRequest request = new ParameterRegisterRequest(
                nonExistentRuleNo, "paramName", "paramValue");

        when(ruleService.getRuleEntity(nonExistentRuleNo))
                .thenThrow(new RuleNotFoundException(nonExistentRuleNo));

        assertThrows(RuleNotFoundException.class, () -> {
            parameterService.registerParameter(request);
        });

        verify(ruleParameterRepository, never()).save(any(RuleParameter.class));
    }

    @Test
    @DisplayName("파라미터 삭제 성공")
    void deleteParameter() {
        Long paramNo = 1L;
        when(ruleParameterRepository.existsById(paramNo)).thenReturn(true);

        parameterService.deleteParameter(paramNo);

        verify(ruleParameterRepository).existsById(paramNo);
        verify(ruleParameterRepository).deleteById(paramNo);
    }

    @Test
    @DisplayName("존재하지 않는 파라미터 삭제 시 예외 발생")
    void deleteParameter_withNonExistentParameter_throwsException() {
        Long nonExistentParamNo = 999L;
        when(ruleParameterRepository.existsById(nonExistentParamNo)).thenReturn(false);

        assertThrows(ParameterNotFoundException.class, () -> {
            parameterService.deleteParameter(nonExistentParamNo);
        });

        verify(ruleParameterRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("파라미터 단건 조회 성공")
    void getParameter() {
        Long paramNo = 1L;

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("group10", "description10", 10);
        Rule mockRule = Rule.ofNewRule(ruleGroup, "rule100", "description100", 100);
        setField(mockRule, "ruleNo", 10L);

        RuleParameter mockParameter = RuleParameter.ofNewRuleParameter(mockRule, "testParam", "testValue");
        setField(mockParameter, "paramNo", paramNo);

        when(ruleParameterRepository.findById(paramNo)).thenReturn(Optional.of(mockParameter));

        ParameterResponse response = parameterService.getParameter(paramNo);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(paramNo, response.getParamNo()),
                () -> assertEquals(mockRule.getRuleNo(), response.getRuleNo()),
                () -> assertEquals("testParam", response.getParamName()),
                () -> assertEquals("testValue", response.getParamValue())
        );
    }

    @Test
    @DisplayName("룰별 파라미터 목록 조회 성공")
    void getParametersByRule() {
        Long ruleNo = 1L;

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("group10", "description10", 10);
        Rule mockRule = Rule.ofNewRule(ruleGroup, "rule100", "description100", 100);
        setField(mockRule, "ruleNo", 20L);

        List<RuleParameter> mockParameters = new ArrayList<>();

        RuleParameter param1 = RuleParameter.ofNewRuleParameter(mockRule, "param1", "value1");
        setField(param1, "paramNo", 1L);

        RuleParameter param2 = RuleParameter.ofNewRuleParameter(mockRule, "param2", "value2");
        setField(param2, "paramNo", 2L);

        mockParameters.add(param1);
        mockParameters.add(param2);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(ruleParameterRepository.findByRule(mockRule)).thenReturn(mockParameters);

        List<ParameterResponse> responses = parameterService.getParametersByRule(ruleNo);

        assertNotNull(responses);
        assertAll(
                () -> assertEquals(2, responses.size()),
                () -> assertEquals("param1", responses.get(0).getParamName()),
                () -> assertEquals("param2", responses.get(1).getParamName())
        );
    }

    @Test
    @DisplayName("파라미터 값 바인딩 성공")
    void bindParameterValue() {
        Long paramNo = 1L;
        String newValue = "newValue";

        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup("group10", "description10", 10);
        Rule mockRule = Rule.ofNewRule(ruleGroup, "rule100", "description100", 100);
        setField(mockRule, "ruleNo", 30L);

        RuleParameter mockParameter = RuleParameter.ofNewRuleParameter(mockRule, "testParam", "oldValue");
        setField(mockParameter, "paramNo", paramNo);

        when(ruleParameterRepository.findById(paramNo)).thenReturn(Optional.of(mockParameter));

        parameterService.bindParameterValue(paramNo, newValue);

        assertEquals(newValue, mockParameter.getParamValue());
        verify(ruleParameterRepository).findById(paramNo);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}