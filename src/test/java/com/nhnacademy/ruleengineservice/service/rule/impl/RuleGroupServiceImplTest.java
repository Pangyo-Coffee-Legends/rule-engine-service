package com.nhnacademy.ruleengineservice.service.rule.impl;

import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupResponse;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupAlreadyExistsException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupNotFoundException;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
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

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class RuleGroupServiceImplTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @InjectMocks
    private RuleGroupServiceImpl ruleGroupService;

    @Test
    @DisplayName("registerRuleGroup - success")
    void registerRuleGroup() {
        String groupName = "test group";
        String description = "test description";
        Integer priority = 1;

        RuleGroupRegisterRequest request = new RuleGroupRegisterRequest(groupName, description, priority);

        Mockito.when(ruleGroupRepository.existsByRuleGroupName(Mockito.anyString())).thenReturn(false);

        RuleGroup group = RuleGroup.ofNewRuleGroup(groupName, description, priority);

        Mockito.when(ruleGroupRepository.save(Mockito.any())).thenReturn(group);

        RuleGroupResponse response = ruleGroupService.registerRuleGroup(request);
        log.debug("registerRuleGroup : {}", response);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(groupName, response.getRuleGroupName()),
                () -> assertEquals(description, response.getRuleGroupDescription()),
                () -> assertEquals(priority, response.getPriority())
        );
    }

    @Test
    @DisplayName("registerRuleGroup - exception")
    void registerRuleGroup_exception() {
        String duplicateName = "중복된 그룹명";

        RuleGroupRegisterRequest request = new RuleGroupRegisterRequest(duplicateName, "설명", 1);

        Mockito.when(ruleGroupRepository.existsByRuleGroupName(Mockito.anyString())).thenReturn(true);

        assertThrows(RuleGroupAlreadyExistsException.class, () -> {
            ruleGroupService.registerRuleGroup(request);
        });

        Mockito.verify(ruleGroupRepository, Mockito.times(1)).existsByRuleGroupName(Mockito.anyString());
    }

    @Test
    @DisplayName("deleteRuleGroup - success")
    void deleteRuleGroup() {
        Long ruleGroupNo = 1L;

        Mockito.when(ruleGroupRepository.existsById(Mockito.anyLong())).thenReturn(true);

        ruleGroupService.deleteRuleGroup(ruleGroupNo);

        Mockito.verify(ruleGroupRepository, Mockito.times(1)).deleteById(ruleGroupNo);
    }

    @Test
    @DisplayName("deleteRuleGroup - exception")
    void deleteRuleGroup_exception() {
        Long ruleGroupNo = 2323L;

        Mockito.when(ruleGroupRepository.existsById(ruleGroupNo)).thenReturn(false);

        assertThrows(RuleGroupNotFoundException.class, () -> {
            ruleGroupService.deleteRuleGroup(ruleGroupNo);
        });

        Mockito.verify(ruleGroupRepository, Mockito.never()).deleteById(ruleGroupNo);
    }

    @Test
    @DisplayName("getRuleGroup - success")
    void getRuleGroup() {
        RuleGroup ruleGroup = RuleGroup.ofNewRuleGroup(
                "rule group",
                "rule description",
                1
        );
        setField(ruleGroup, "ruleGroupNo", 1L);

        Mockito.when(ruleGroupRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(ruleGroup));

        RuleGroupResponse response = ruleGroupService.getRuleGroup(1L);
        log.debug("getRuleGroup : {}", response);

        assertNotNull(response);
        assertAll(
                () -> assertEquals(1L, response.getRuleGroupNo()),
                () -> assertEquals("rule group", response.getRuleGroupName()),
                () -> assertEquals("rule description", response.getRuleGroupDescription()),
                () -> assertEquals(1, response.getPriority())
        );
    }

    @Test
    @DisplayName("getRuleGroup - exception")
    void getRuleGroup_exception() {
        Long ruleGroupNo = 1352L;

        Mockito.when(ruleGroupRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(RuleGroupNotFoundException.class, () -> {
            ruleGroupService.getRuleGroup(ruleGroupNo);
        });
    }

    @Test
    @DisplayName("getAllRuleGroups - success")
    void getAllRuleGroups() {
        List<RuleGroup> mockList = new ArrayList<>();

        RuleGroup group1 = RuleGroup.ofNewRuleGroup("R1","D1",1);
        RuleGroup group2 = RuleGroup.ofNewRuleGroup("R2","D2",2);

        mockList.add(group1);
        mockList.add(group2);

        Mockito.when(ruleGroupRepository.findAll()).thenReturn(mockList);

        List<RuleGroupResponse> responses = ruleGroupService.getAllRuleGroups();
        log.debug("getAllRuleGroups : {}", responses);

        assertNotNull(responses);
        assertAll(
                () -> assertEquals(2, responses.size()),
                () -> assertEquals("R1", responses.get(0).getRuleGroupName()),
                () -> assertEquals("R2", responses.get(1).getRuleGroupName())
        );
    }

    @Test
    @DisplayName("getAllRuleGroups - exception")
    void getAllRuleGroups_exception() {
        Mockito.when(ruleGroupRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(RuleGroupNotFoundException.class, () -> {
            ruleGroupService.getAllRuleGroups();
        });
    }

    @Test
    @DisplayName("setRuleGroupActive - success")
    void setRuleGroupActive() {
        Long ruleGroupNo = 1L;
        boolean newActive = false;

        RuleGroup group = RuleGroup.ofNewRuleGroup("R1","D1",1);

        Mockito.when(ruleGroupRepository.findById(ruleGroupNo)).thenReturn(Optional.of(group));
        Mockito.when(ruleGroupRepository.save(Mockito.any())).thenReturn(group);

        ruleGroupService.setRuleGroupActive(ruleGroupNo, newActive);

        assertEquals(newActive, group.isActive());
        Mockito.verify(ruleGroupRepository, Mockito.times(1)).save(group);
    }

    @Test
    @DisplayName("setRuleGroupActive - exception")
    void setRuleGroupActive_exception() {
        Long ruleGroupNo = 1425L;
        boolean newActive = true;

        Mockito.when(ruleGroupRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(RuleGroupNotFoundException.class, () -> {
            ruleGroupService.setRuleGroupActive(ruleGroupNo, newActive);
        });

        Mockito.verify(ruleGroupRepository, Mockito.never()).save(Mockito.any());
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