package com.nhnacademy.ruleengineservice.service.rule.impl;

import com.nhnacademy.ruleengineservice.adaptor.MemberAdaptor;
import com.nhnacademy.ruleengineservice.auth.MemberThreadLocal;
import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.domain.rule.RuleMemberMapping;
import com.nhnacademy.ruleengineservice.domain.schedule.RuleSchedule;
import com.nhnacademy.ruleengineservice.domain.trigger.TriggerEvent;
import com.nhnacademy.ruleengineservice.dto.member.MemberResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleUpdateRequest;
import com.nhnacademy.ruleengineservice.exception.auth.AccessDeniedException;
import com.nhnacademy.ruleengineservice.exception.member.MemberNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RulePersistException;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleMemberMappingRepository;
import com.nhnacademy.ruleengineservice.repository.rule.RuleRepository;
import com.nhnacademy.ruleengineservice.repository.trigger.TriggerRepository;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RuleGroupRepository ruleGroupRepository;

    private final RuleRepository ruleRepository;

    private final RuleMemberMappingRepository ruleMemberMappingRepository;

    private final MemberAdaptor memberAdaptor;

    private final TriggerRepository triggerRepository;

    @Override
    public RuleResponse registerRule(RuleRegisterRequest request) {
        RuleGroup ruleGroup = ruleGroupRepository.findById(request.getRuleGroupNo())
                .orElseThrow(() -> new RuleGroupNotFoundException(request.getRuleGroupNo()));

        MemberResponse member = validateMember();
        log.debug("registerRule member : {}", member);

        Rule rule = ruleRepository.save(
                Rule.ofNewRule(
                        ruleGroup,
                        request.getRuleName(),
                        request.getRuleDescription(),
                        request.getRulePriority()
                )
        );
        log.debug("registerRule rule : {}", rule);

        TriggerEvent trigger = triggerRepository.save(TriggerEvent.ofNewTriggerEvent(rule, "AI_DATA_RECEIVED", "{\"source\":\"AI\"}"));
        log.debug("registerRule trigger : {}", trigger);

        rule.getTriggerEventList().add(trigger);


        try {
            ruleMemberMappingRepository.save(
                    RuleMemberMapping.ofNewRuleMemberMapping(
                            rule,
                            member.getNo()
                    )
            );
        } catch (DataAccessException e) {
            log.error("registerRule mapping failed");
            throw new RulePersistException("rule member mapping failed : " + e);
        }

        return toRuleResponse(ruleRepository.save(rule));
    }

    @Override
    public RuleResponse updateRule(Long ruleNo, RuleUpdateRequest request) {
        MemberResponse member = validateMember();
        checkAdminRole(member);

        Rule rule = ruleRepository.findById(ruleNo)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));

        rule.ruleUpdate(
                request.getRuleName(),
                request.getRuleDescription(),
                request.getRulePriority()
        );

        log.debug("updateRule : {}", rule);

        return toRuleResponse(rule);
    }

    @Override
    public void deleteRule(Long ruleNo) {
        MemberResponse member = validateMember();
        checkAdminRole(member);

        if (!ruleRepository.existsById(ruleNo)) {
            log.error("deleteRule rule not found");
            throw new RuleNotFoundException(ruleNo);
        }

        Rule rule = ruleRepository.findById(ruleNo)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));

        if (!rule.getActionList().isEmpty() ||
                !rule.getConditionList().isEmpty() ||
                !rule.getRuleParameterList().isEmpty()) {
            throw new IllegalStateException("하위 요소가 남아 있어 삭제할 수 없습니다.");
        }

        ruleMemberMappingRepository.deleteByRule_RuleNo(ruleNo);

        triggerRepository.deleteAll(rule.getTriggerEventList());

        ruleRepository.delete(rule);
        log.info("Rule {} and its mappings deleted.", ruleNo);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleResponse getRule(Long ruleNo) {
        validateMember();

        log.debug("getRule start");

        return ruleRepository.findById(ruleNo)
                .map(this::toRuleResponse)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getAllRule() {
        validateMember();

        List<Rule> ruleList = ruleRepository.findAll();

        if (ruleList.isEmpty()) {
            log.error("getAllRule rule not found");
            throw new RuleNotFoundException("Rule Not Found");
        }

        log.debug("getAllRule ruleList : {}", ruleList);
        return ruleList.stream()
                .map(this::toRuleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleResponse> getRulesByGroup(Long ruleGroupNo) {
        validateMember();

        RuleGroup ruleGroup = ruleGroupRepository.findById(ruleGroupNo)
                .orElseThrow(() -> new RuleGroupNotFoundException(ruleGroupNo));

        List<Rule> ruleList = ruleRepository.findByRuleGroup(ruleGroup);
        log.debug("getRulesByGroup list : {}", ruleList);

        return ruleList.stream()
                .map(this::toRuleResponse)
                .toList();
    }

    @Override
    public void setRuleActive(Long ruleNo, boolean active) {
        Rule rule = ruleRepository.findById(ruleNo)
                .orElseThrow(() -> new RuleNotFoundException(ruleNo));

        rule.setActive(active);

        log.debug("setRuleActive rule : {}", rule);
        ruleRepository.save(rule);
    }

    @Override
    public Rule getRuleEntity(Long ruleNo) {
        log.debug("getRuleEntity start");

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

    private MemberResponse validateMember() {
        String email = MemberThreadLocal.getMemberEmail();
        ResponseEntity<MemberResponse> response = memberAdaptor.getMemberByEmail(email);

        if (response == null || response.getBody() == null) {
            log.error("Member not found: {}", email);
            throw new MemberNotFoundException(email);
        }

        return response.getBody();
    }

    private void checkAdminRole(MemberResponse member) {
        if (!"ROLE_ADMIN".equals(member.getRoleName())) {
            log.error("Access denied. Required role: ROLE_ADMIN, Current role: {}", member.getRoleName());
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }
}