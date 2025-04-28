package com.nhnacademy.ruleengineservice.domain;

import com.nhnacademy.ruleengineservice.domain.rule.RuleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleStatusTest {

    @Test
    @DisplayName("String 값 확인")
    void testEnumValues() {
        assertEquals("활성", RuleStatus.ENABLED.getName());
        assertEquals("비활성", RuleStatus.DISABLED.getName());
        assertEquals("검증 대기", RuleStatus.PENDING.getName());
        assertEquals("삭제", RuleStatus.DELETE.getName());
    }

    @Test
    @DisplayName("value 값 확인")
    void testValueOf() {
        assertEquals(RuleStatus.ENABLED, RuleStatus.valueOf("ENABLED"));
        assertEquals(RuleStatus.DISABLED, RuleStatus.valueOf("DISABLED"));
        assertEquals(RuleStatus.PENDING, RuleStatus.valueOf("PENDING"));
        assertEquals(RuleStatus.DELETE, RuleStatus.valueOf("DELETE"));
    }

    @Test
    @DisplayName("enum 개수 확인")
    void testValueLength() {
        assertEquals(4, RuleStatus.values().length);
    }
}