package com.nhnacademy.ruleengineservice.service.rule.impl;

import com.nhnacademy.ruleengineservice.domain.rule.RuleGroup;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupResponse;
import com.nhnacademy.ruleengineservice.dto.rule.RuleGroupUpdateRequest;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupAlreadyExistsException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleGroupNotFoundException;
import com.nhnacademy.ruleengineservice.repository.rule.RuleGroupRepository;
import com.nhnacademy.ruleengineservice.service.rule.RuleGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class RuleGroupServiceImpl implements RuleGroupService {

    private final RuleGroupRepository ruleGroupRepository;

    public RuleGroupServiceImpl(RuleGroupRepository ruleGroupRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
    }

    @Override
    public RuleGroupResponse registerRuleGroup(RuleGroupRegisterRequest request) {
        if (ruleGroupRepository.existsByRuleGroupName(request.getRuleGroupName())) {
            log.error("registerRuleGroup already exists");
            throw new RuleGroupAlreadyExistsException(request.getRuleGroupName());
        }

        RuleGroup group = RuleGroup.ofNewRuleGroup(
                request.getRuleGroupName(),
                request.getRuleGroupDescription(),
                request.getPriority()
        );
        log.debug("registerRuleGroup group : {}", group);

        return toRuleGroupResponse(ruleGroupRepository.save(group));
    }

    @Override
    public RuleGroupResponse updateRuleGroup(Long no, RuleGroupUpdateRequest request) {
        RuleGroup ruleGroup = ruleGroupRepository.findById(no)
                .orElseThrow(() -> new RuleGroupNotFoundException(no));

        ruleGroup.ruleGroupUpdate(
                request.getRuleGroupName(),
                request.getRuleGroupDescription(),
                request.getPriority()
        );

        log.debug("update rule group : {}", ruleGroup);

        return toRuleGroupResponse(ruleGroup);
    }

    @Override
    public void deleteRuleGroup(Long ruleGroupNo) {
        if (!ruleGroupRepository.existsById(ruleGroupNo)) {
            log.error("deleteRuleGroup group not found");
            throw new RuleGroupNotFoundException(ruleGroupNo);
        }

        ruleGroupRepository.deleteById(ruleGroupNo);
        log.debug("deleteRuleGroup success");
    }

    @Override
    @Transactional(readOnly = true)
    public RuleGroupResponse getRuleGroup(Long ruleGroupNo) {
        log.debug("getRuleGroup start");

        return ruleGroupRepository.findById(ruleGroupNo)
                .map(this::toRuleGroupResponse)
                .orElseThrow(() -> new RuleGroupNotFoundException(ruleGroupNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleGroupResponse> getAllRuleGroups() {
        List<RuleGroup> ruleGroupList = ruleGroupRepository.findAll();

        if (ruleGroupList.isEmpty()) {
            log.error("getAllRuleGroups group not found");
            throw new RuleGroupNotFoundException("Rule Groups Not Found");
        }

        log.debug("getAllRuleGroups groupList : {}", ruleGroupList);

        // 엔티티를 DTO로 변환
        return ruleGroupList.stream()
                .map(this::toRuleGroupResponse)
                .toList();
    }

    @Override
    public void setRuleGroupActive(Long ruleGroupNo, boolean active) {
        RuleGroup ruleGroup = ruleGroupRepository.findById(ruleGroupNo)
                .orElseThrow(() -> new RuleGroupNotFoundException(ruleGroupNo));

        ruleGroup.setActive(active);

        log.debug("setRuleGroupActive : {}", ruleGroup);
        ruleGroupRepository.save(ruleGroup);
    }

    /**
     * RuleGroupResponse 로 변환하기 위한 메서드 입니다.
     *
     * @param ruleGroup 변경하고자 하는 rule group
     * @return 새 rule group response DTO
     */
    private RuleGroupResponse toRuleGroupResponse(RuleGroup ruleGroup) {
        return new RuleGroupResponse(
                ruleGroup.getRuleGroupNo(),
                ruleGroup.getRuleGroupName(),
                ruleGroup.getRuleGroupDescription(),
                ruleGroup.getPriority(),
                ruleGroup.isActive()
        );
    }
}
