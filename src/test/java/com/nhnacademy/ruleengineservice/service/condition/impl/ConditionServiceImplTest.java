package com.nhnacademy.ruleengineservice.service.condition.impl;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResult;
import com.nhnacademy.ruleengineservice.exception.condition.ConditionNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.condition.ConditionRepository;
import com.nhnacademy.ruleengineservice.service.rule.impl.RuleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class ConditionServiceImplTest {

    @Mock
    private ConditionRepository conditionRepository;

    @Mock
    private RuleServiceImpl ruleService;

    @InjectMocks
    private ConditionServiceImpl conditionService;

    Rule rule;

    @BeforeEach
    void setUp() {
        rule = mock();
    }

    @Test
    @DisplayName("조건 등록 성공")
    void registerCondition() {
        Long ruleNo = 1L;
        String conType = "EQ";
        String conField = "temperature";
        String conValue = "25";
        Integer conPriority = 1;

        ConditionRegisterRequest request = new ConditionRegisterRequest(
                ruleNo, conType, conField, conValue, conPriority);

        Condition savedCondition = Condition.ofNewCondition(rule, conType, conField, conValue, conPriority);
        setField(savedCondition, "conditionNo", 1L);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(rule);
        when(conditionRepository.save(Mockito.any(Condition.class))).thenReturn(savedCondition);

        ConditionResponse response = conditionService.registerCondition(request);
        log.debug("register condition : {}", response);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(1L, response.getConditionNo()),
                () -> assertEquals(conType, response.getConType())
        );

        verify(ruleService).getRuleEntity(ruleNo);
        verify(conditionRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("조건 등록 실패")
    void registerCondition_exception() {
        Long nonExistentRuleNo = 1365L;
        ConditionRegisterRequest request = new ConditionRegisterRequest(
                nonExistentRuleNo, "EQ", "field", "value", 1);

        when(ruleService.getRuleEntity(nonExistentRuleNo))
                .thenThrow(new RuleNotFoundException(nonExistentRuleNo));

        assertThrows(RuleNotFoundException.class, () -> conditionService.registerCondition(request));

        verify(conditionRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("조건 삭제 성공")
    void deleteCondition() {
        Long conditionNo = 1L;
        when(conditionRepository.existsById(conditionNo)).thenReturn(true);

        conditionService.deleteCondition(conditionNo);

        verify(conditionRepository).existsById(conditionNo);
        verify(conditionRepository).deleteById(conditionNo);
    }

    @Test
    @DisplayName("조건 삭제 실패")
    void deleteCondition_withNonExistentCondition_throwsException() {
        Long nonExistentConditionNo = 999L;
        when(conditionRepository.existsById(nonExistentConditionNo)).thenReturn(false);

        assertThrows(ConditionNotFoundException.class, () -> conditionService.deleteCondition(nonExistentConditionNo));
        verify(conditionRepository, Mockito.never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("조건 단건 조회 성공")
    void getCondition() {
        Long conditionNo = 1L;

        Condition mockCondition = Condition.ofNewCondition(
                rule,
                "EQ",
                "temperature",
                "25",
                1
        );
        setField(mockCondition, "conditionNo", conditionNo);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(mockCondition));

        ConditionResponse response = conditionService.getCondition(conditionNo);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(conditionNo, response.getConditionNo()),
                () -> assertEquals(rule.getRuleNo(), response.getRuleNo()),
                () -> assertEquals("EQ", response.getConType()),
                () -> assertEquals("temperature", response.getConField()),
                () -> assertEquals("25", response.getConValue()),
                () -> assertEquals(1, response.getConPriority())
        );
    }

    @Test
    void getConditionsByRule_ValidRuleNo_ReturnsConditionResponses() {
        Long ruleNo = 1L;
        RuleGroup ruleGroup = mock();
        Rule mockRule = Rule.ofNewRule(ruleGroup, "Test Rule", "Description", 1);
        setField(mockRule, "ruleNo", ruleNo);
        Condition condition1 = Condition.ofNewCondition(mockRule, "type1", "field1", "value1", 1);
        Condition condition2 = Condition.ofNewCondition(mockRule, "type2", "field2", "value2", 2);
        List<Condition> mockConditions = List.of(condition1, condition2);

        when(ruleService.getRuleEntity(ruleNo)).thenReturn(mockRule);
        when(conditionRepository.findByRule(mockRule)).thenReturn(mockConditions);

        List<ConditionResponse> result = conditionService.getConditionsByRule(ruleNo);

        assertEquals(2, result.size());
        assertEquals("field1", result.get(0).getConField());
        assertEquals("type2", result.get(1).getConType());

        verify(ruleService).getRuleEntity(ruleNo);
        verify(conditionRepository).findByRule(mockRule);
    }

    @Test
    void getConditions_ReturnsAllConditionResponses() {
        Condition condition1 = Condition.ofNewCondition(rule, "type1", "field1", "value1", 1);
        Condition condition2 = Condition.ofNewCondition(rule, "type2", "field2", "value2", 2);
        List<Condition> mockConditions = List.of(condition1, condition2);

        when(conditionRepository.findAll()).thenReturn(mockConditions);

        List<ConditionResponse> result = conditionService.getConditions();

        assertEquals(2, result.size());
        assertEquals("field2", result.get(1).getConField());
        verify(conditionRepository).findAll();
    }

    @Test
    @DisplayName("조건 단건 조회 실패")
    void getCondition_exception() {
        Long nonExistentConditionNo = 1235L;
        when(conditionRepository.findById(nonExistentConditionNo)).thenReturn(Optional.empty());

        assertThrows(ConditionNotFoundException.class, () -> conditionService.getCondition(nonExistentConditionNo));
    }

    @Test
    @DisplayName("조건 평가 - EQ 성공")
    void evaluateCondition_EQ_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", "25");

        Condition condition = Condition.ofNewCondition(rule, "EQ", "temperature", "25", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - GT 성공")
    void evaluateCondition_GT_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", "30");

        Condition condition = Condition.ofNewCondition(rule, "GT", "temperature", "25", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - LT 성공")
    void evaluateCondition_LT_ValidLessThan_ReturnsTrue() {
        Condition condition = Condition.ofNewCondition(rule, "LT", "temperature", "10.5", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("temperature", "9.8");
        assertTrue(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - LT 실패")
    void evaluateCondition_LT_InvalidFormat_ReturnsFalse() {
        Condition condition = Condition.ofNewCondition(rule, "LT", "temperature", "abc", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("temperature", "9.8");
        assertFalse(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - LTE 성공")
    void evaluateCondition_LTE_EqualValue_ReturnsTrue() {
        Condition condition = Condition.ofNewCondition(rule, "LTE", "score", "100", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("score", "100");
        assertTrue(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - LTE 실패")
    void evaluateCondition_LTE_GreaterValue_ReturnsFalse() {
        Condition condition = Condition.ofNewCondition(rule, "LTE", "score", "50", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("score", "51");
        assertFalse(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - IN 성공")
    void evaluateCondition_IN_ContainsSubstring_ReturnsTrue() {
        Condition condition = Condition.ofNewCondition(rule, "IN", "fruit", "apple", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("fruit", "pineapple");
        assertTrue(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - IN 실패")
    void evaluateCondition_IN_NotContains_ReturnsFalse() {
        Condition condition = Condition.ofNewCondition(rule, "IN", "fruit", "orange", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("fruit", "apple");
        assertFalse(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - NOT_IN 성공")
    void evaluateCondition_NOT_IN_NotContains_ReturnsTrue() {
        Condition condition = Condition.ofNewCondition(rule, "NOT_IN", "fruit", "banana", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("fruit", "mango");
        assertTrue(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - NOT_IN 실패")
    void evaluateCondition_NOT_IN_Contains_ReturnsFalse() {
        Condition condition = Condition.ofNewCondition(rule, "NOT_IN", "fruit", "grape", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("fruit", "grapefruit");
        assertFalse(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - LIKE 성공")
    void evaluateCondition_LIKE_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("name", "Kim Monsoon");

        Condition condition = Condition.ofNewCondition(rule, "LIKE", "name", "Kim.*", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - NOT_LIKE 성공")
    void evaluateCondition_NOT_LIKE_DoesNotMatch_ReturnsTrue() {
        Condition condition = Condition.ofNewCondition(rule, "NOT_LIKE", "name", "test%", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("name", "example123");
        assertTrue(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - NOT_LIKE 실패")
    void evaluateCondition_NOT_LIKE_Matches_ReturnsFalse() {
        Condition condition = Condition.ofNewCondition(rule, "NOT_LIKE", "name", "user_%", 1);
        when(conditionRepository.findById(anyLong())).thenReturn(Optional.of(condition));

        Map<String, Object> facts = Map.of("name", "user_123");
        assertFalse(conditionService.evaluateCondition(1L, facts));
    }

    @Test
    @DisplayName("조건 평가 - BETWEEN 성공")
    void evaluateCondition_BETWEEN_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("temperature", "25");

        Condition condition = Condition.ofNewCondition(rule, "BETWEEN", "temperature", "20, 30", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - NE(Not Equal) 성공")
    void evaluateCondition_NE_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("status", "active");

        Condition condition = Condition.ofNewCondition(rule, "NE", "status", "inactive", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - GTE(Greater Than or Equal) 성공")
    void evaluateCondition_GTE_success() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("age", "30");

        Condition condition = Condition.ofNewCondition(rule, "GTE", "age", "30", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertTrue(result);
    }

    @Test
    @DisplayName("조건 평가 - 숫자 변환 실패 시 false 반환")
    void evaluateCondition_numberFormatException_returnsFalse() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("price", "one_hundred");

        Condition condition = Condition.ofNewCondition(rule, "GT", "price", "50", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertFalse(result);
    }

    @Test
    @DisplayName("BETWEEN 조건 - 잘못된 형식의 값 처리")
    void evaluateCondition_BETWEEN_invalidFormat() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        facts.put("score", "85");

        Condition condition = Condition.ofNewCondition(rule, "BETWEEN", "score", "invalid_format", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertFalse(result);
    }

    @Test
    @DisplayName("getRequiredFieldsByRule - 중복 필드 제거 확인")
    void getRequiredFieldsByRule_duplicateFields() {
        Condition condition1 = Condition.ofNewCondition(rule, "EQ", "temperature", "25", 1);
        Condition condition2 = Condition.ofNewCondition(rule, "GT", "temperature", "30", 1);
        Condition condition3 = Condition.ofNewCondition(rule, "GT", "humidity", "30", 1);

        when(rule.getConditionList()).thenReturn(Arrays.asList(condition1, condition2, condition3));

        List<String> fields = conditionService.getRequiredFieldsByRule(rule);

        assertEquals(2, fields.size());
        assertEquals("temperature", fields.get(0));
        assertEquals("humidity", fields.get(1));
    }

    @Test
    @DisplayName("필드가 존재하지 않는 조건 평가 시 false 반환")
    void evaluateCondition_withNonExistentField_returnsFalse() {
        Long conditionNo = 1L;
        Map<String, Object> facts = new HashMap<>();
        // facts 에 "temperature" 필드가 없음

        Condition condition = Condition.ofNewCondition(rule, "EQ", "temperature", "25", 1);

        when(conditionRepository.findById(conditionNo)).thenReturn(Optional.of(condition));

        boolean result = conditionService.evaluateCondition(conditionNo, facts);

        assertFalse(result);
    }

    @Test
    @DisplayName("조건 엔티티 검증")
    void getConditionEntity() {
        Long conNo = 1L;

        Condition condition = Condition.ofNewCondition(rule, "EQ", "temperature", "25",1);
        setField(condition, "conditionNo", conNo);

        when(conditionRepository.findById(conNo)).thenReturn(Optional.of(condition));

        Condition result = conditionService.getConditionEntity(conNo);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(conNo, result.getConditionNo()),
                () -> assertEquals("EQ", result.getConType()),
                () -> assertEquals("temperature", result.getConField()),
                () -> assertEquals("25", result.getConValue()),
                () -> assertEquals(1, result.getConPriority())
        );
    }

    @Test
    @DisplayName("존재하지 않는 조건 엔티티 조회 시 예외 발생")
    void getConditionEntity_exception() {
        Long nonExistentConditionNo = 9126L;
        when(conditionRepository.findById(nonExistentConditionNo)).thenReturn(Optional.empty());

        assertThrows(ConditionNotFoundException.class, () -> conditionService.getConditionEntity(nonExistentConditionNo));

        verify(conditionRepository).findById(nonExistentConditionNo);
    }

    @Test
    @DisplayName("규칙에 연결된 조건들을 평가하고 결과 리스트 반환")
    void evaluateConditionsForRule_ReturnsListOfConditionResults() {
        Rule mockRule = mock();
        setField(mockRule, "ruleNo", 1L);

        Condition condition1 = Condition.ofNewCondition(mockRule, "LT", "temperature", "30", 1);
        setField(condition1, "conditionNo", 101L);

        Condition condition2 = Condition.ofNewCondition(mockRule, "EQ", "status", "active", 1);
        setField(condition2, "conditionNo", 102L);

        List<Condition> conditions = List.of(condition1, condition2);

        when(conditionRepository.findByRule(mockRule)).thenReturn(conditions);
        when(conditionRepository.findById(101L)).thenReturn(Optional.of(condition1));
        when(conditionRepository.findById(102L)).thenReturn(Optional.of(condition2));

        Map<String, Object> facts = Map.of(
                "temperature", "25",
                "status", "active"
        );

        List<ConditionResult> results = conditionService.evaluateConditionsForRule(mockRule, facts);

        assertEquals(2, results.size());

        ConditionResult result1 = results.get(0);
        assertAll(
                () -> assertEquals(101L, result1.getConNo()),
                () -> assertEquals("temperature", result1.getConField()),
                () -> assertEquals("LT", result1.getConType()),
                () -> assertEquals("30", result1.getConValue()),
                () -> assertTrue(result1.isMatched()) // 25 < 30 → true
        );

        ConditionResult result2 = results.get(1);
        assertAll(
                () -> assertEquals(102L, result2.getConNo()),
                () -> assertEquals("status", result2.getConField()),
                () -> assertEquals("EQ", result2.getConType()),
                () -> assertEquals("active", result2.getConValue()),
                () -> assertTrue(result2.isMatched()) // "active" == "active" → true
        );
    }

    @Test
    @DisplayName("조건이 없는 경우 빈 리스트 반환")
    void evaluateConditionsForRule_NoConditions_ReturnsEmptyList() {
        when(conditionRepository.findByRule(rule)).thenReturn(Collections.emptyList());

        List<ConditionResult> results = conditionService.evaluateConditionsForRule(rule, Map.of());

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("일부 조건 실패 시 결과 반환")
    void evaluateConditionsForRule_MixedResults_ReturnsCorrectResults() {
        Condition condition1 = Condition.ofNewCondition(rule, "GT", "score", "80", 1);
        setField(condition1, "conditionNo", 103L);

        Condition condition2 = Condition.ofNewCondition(rule, "IN", "role", "admin", 1);
        setField(condition2, "conditionNo", 104L);

        when(conditionRepository.findByRule(rule)).thenReturn(List.of(condition1, condition2));
        when(conditionRepository.findById(103L)).thenReturn(Optional.of(condition1));
        when(conditionRepository.findById(104L)).thenReturn(Optional.of(condition1));

        Map<String, Object> facts = Map.of(
                "score", "75",    // 75 > 80 → false
                "role", "user"    // "user" contains "admin" → false
        );

        List<ConditionResult> results = conditionService.evaluateConditionsForRule(rule, facts);

        assertAll(
                () -> assertFalse(results.get(0).isMatched()),
                () -> assertFalse(results.get(1).isMatched())
        );
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