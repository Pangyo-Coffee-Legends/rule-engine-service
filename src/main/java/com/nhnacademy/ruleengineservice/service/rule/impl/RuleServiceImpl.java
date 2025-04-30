package com.nhnacademy.ruleengineservice.service.rule.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.domain.schedule.RuleSchedule;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RuleServiceImpl implements RuleService {

    private final RuleGroupRepository ruleGroupRepository;

    private final RuleRepository ruleRepository;

    public RuleServiceImpl(RuleGroupRepository ruleGroupRepository,
                           RuleRepository ruleRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleRepository = ruleRepository;
    }

    @Override
    public RuleResponse registerRule(RuleRegisterRequest request) {
        RuleGroup ruleGroup = ruleGroupRepository.findById(request.getRuleGroupNo())
                .orElseThrow(() -> new RuleGroupNotFoundException(request.getRuleGroupNo()));

        Rule rule = Rule.ofNewRule(
                ruleGroup,
                request.getRuleName(),
                request.getRuleDescription(),
                request.getRulePriority()
        );

        return toRuleResponse(ruleRepository.save(rule));
    }

    @Override
    public RuleResponse updateRule(Long ruleNo, RuleUpdateRequest request) {
        Rule rule = ruleRepository.findById(ruleNo)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));

        rule.ruleUpdate(
                request.getRuleName(),
                request.getRuleDescription(),
                request.getRulePriority()
        );

        return toRuleResponse(rule);
    }

    @Override
    public void deleteRule(Long ruleNo) {
        if (!ruleRepository.existsById(ruleNo)) {
            throw new RuleNotFoundException(ruleNo);
        }

        ruleRepository.deleteById(ruleNo);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleResponse getRule(Long ruleNo) {
        return ruleRepository.findById(ruleNo)
                .map(this::toRuleResponse)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getAllRule() {
        List<Rule> ruleList = ruleRepository.findAll();

        if (ruleList.isEmpty()) {
            throw new RuleNotFoundException("Rule Not Found");
        }

        return ruleList.stream()
                .map(this::toRuleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getRulesByGroup(Long ruleGroupNo) {
        RuleGroup ruleGroup = ruleGroupRepository.findById(ruleGroupNo)
                .orElseThrow(() -> new RuleGroupNotFoundException(ruleGroupNo));

        List<Rule> ruleList = ruleRepository.findByRuleGroup(ruleGroup);

        return ruleList.stream()
                .map(this::toRuleResponse)
                .toList();
    }

    @Override
    public void setRuleActive(Long ruleNo, boolean active) {
        Rule rule = ruleRepository.findById(ruleNo)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));

        rule.setActive(active);
        ruleRepository.save(rule);
    }

    @Override
    public Rule getRuleEntity(Long ruleNo) {
        return ruleRepository.findById(ruleNo)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));
    }

    private RuleResponse toRuleResponse(Rule rule) {
        return new RuleResponse(
                rule.getRuleNo(),
                rule.getRuleName(),
                rule.getRuleDescription(),
                rule.getRulePriority(),
                rule.isActive(),
                rule.getRuleGroup().getRuleGroupNo(),
                rule.getActionList().stream()
                        .map(Action::getActNo)
                        .toList(),
                rule.getConditionList().stream()
                        .map(Condition::getConditionNo)
                        .toList(),
                rule.getRuleParameterList().stream()
                        .map(RuleParameter::getParamNo)
                        .toList(),
                rule.getRuleScheduleList().stream()
                        .map(RuleSchedule::getScheduleNo)
                        .toList(),
                rule.getTriggerEventList().stream()
                        .map(TriggerEvent::getEventNo)
                        .toList()
        );
    }
}
