package com.nhnacademy.ruleengineservice.service.condition.impl;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResult;
import com.nhnacademy.ruleengineservice.exception.condition.ConditionNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
import com.nhnacademy.ruleengineservice.repository.condition.ConditionRepository;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class ConditionServiceImpl implements ConditionService {

    private final ConditionRepository conditionRepository;

    private final RuleService ruleService;

    public ConditionServiceImpl(ConditionRepository conditionRepository, RuleService ruleService) {
        this.conditionRepository = conditionRepository;
        this.ruleService = ruleService;
    }

    @Override
    public ConditionResponse registerCondition(ConditionRegisterRequest request) {
        Rule rule = ruleService.getRuleEntity(request.getRuleNo());

        Condition condition = Condition.ofNewCondition(
                rule,
                request.getConType(),
                request.getConField(),
                request.getConValue(),
                request.getConPriority()
        );

        rule.getConditionList().add(condition);

        log.debug("registerCondition : {}", condition);

        return toConditionResponse(conditionRepository.save(condition));
    }

    @Override
    public void deleteCondition(Long conditionNo) {
        if(!conditionRepository.existsById(conditionNo)) {
            log.error("deleteCondition condition not found");
            throw new ConditionNotFoundException(conditionNo);
        }

        conditionRepository.deleteById(conditionNo);
        log.debug("deleteCondition success");
    }

    @Override
    public void deleteConditionByRuleNoAndConditionNo(Long ruleNo, Long conditionNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);
        if (Objects.isNull(rule)) {
            throw new RuleNotFoundException(ruleNo);
        }

        Condition condition = conditionRepository.findById(conditionNo)
                .orElseThrow(() -> new ConditionNotFoundException(conditionNo));

        if (!condition.getRule().getRuleNo().equals(ruleNo)) {
            throw new IllegalArgumentException("Condition does not belong to the specified rule.");
        }

        conditionRepository.delete(condition);
        log.debug("Condition {} associated with ruleNo = {} has been deleted.", conditionNo, ruleNo);
    }

    @Override
    public void deleteConditionByRule(Long ruleNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);

        if (Objects.isNull(rule)) {
            throw new RuleNotFoundException(ruleNo);
        }

        List<Condition> conditionList = conditionRepository.findByRule(rule);

        if (conditionList.isEmpty()) {
            throw new ConditionNotFoundException("condition is null");
        }

        conditionRepository.deleteAll(conditionList);
        log.debug("{} conditions associated with ruleNo = {} have been deleted.", ruleNo, conditionList.size());
    }

    @Override
    @Transactional(readOnly = true)
    public ConditionResponse getCondition(Long conditionNo) {
        log.debug("getCondition start");

        return conditionRepository.findById(conditionNo)
                .map(this::toConditionResponse)
                .orElseThrow(() -> new ConditionNotFoundException(conditionNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getConditionsByRule(Long ruleNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);

        List<Condition> conditionList = conditionRepository.findByRule(rule);

        log.debug("conditionList : {}", conditionList);

        return conditionList.stream()
                .map(this::toConditionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getConditions() {
        List<Condition> conditionList = conditionRepository.findAll();

        log.debug("get condition list : {}", conditionList);
        return conditionList.stream()
                .map(this::toConditionResponse)
                .toList();
    }

    @Override
    public boolean evaluateCondition(Long conditionNo, Map<String, Object> facts) {
        Condition condition = conditionRepository.findById(conditionNo)
                .orElseThrow(() -> new ConditionNotFoundException(conditionNo));

        log.debug("evaluateCondition condition : {}", condition);

        Object factValue = facts.get(condition.getConField());
        if (factValue == null) {
            return false;
        }

        log.debug("evaluateCondition factValue : {}", factValue);

        switch (condition.getConType()) {
            case "EQ":
                return factValue.toString().equals(condition.getConValue());
            case "NE":
                return !factValue.toString().equals(condition.getConValue());
            case "GT":
                try {
                    double factDouble = Double.parseDouble(factValue.toString());
                    double conditionDouble = Double.parseDouble(condition.getConValue());
                    return factDouble > conditionDouble;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "LT":
                try {
                    double factDouble = Double.parseDouble(factValue.toString());
                    double conditionDouble = Double.parseDouble(condition.getConValue());
                    return factDouble < conditionDouble;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "GTE":
                try {
                    double factDouble = Double.parseDouble(factValue.toString());
                    double conditionDouble = Double.parseDouble(condition.getConValue());
                    return factDouble >= conditionDouble;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "LTE":
                try {
                    double factDouble = Double.parseDouble(factValue.toString());
                    double conditionDouble = Double.parseDouble(condition.getConValue());
                    return factDouble <= conditionDouble;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "IN":
                return factValue.toString().contains(condition.getConValue());
            case "NOT_IN":
                return !factValue.toString().contains(condition.getConValue());
            case "LIKE":
                // SQL의 LIKE 패턴을 정규식으로 변환 (% -> .*)
                String likePattern = condition.getConValue().replace("%", ".*");
                return factValue.toString().matches(likePattern);
            case "NOT_LIKE":
                String notLikePattern = condition.getConValue().replace("%", ".*");
                return !factValue.toString().matches(notLikePattern);
            case "BETWEEN":
                try {
                    String[] bounds = condition.getConValue().split(",");
                    double lowerBound = Double.parseDouble(bounds[0].trim());
                    double upperBound = Double.parseDouble(bounds[1].trim());
                    double factDoubleValue = Double.parseDouble(factValue.toString());
                    return factDoubleValue >= lowerBound && factDoubleValue <= upperBound;
                } catch (Exception e) {
                    return false;
                }
            default:
                return false;
        }
    }

    @Override
    public Condition getConditionEntity(Long conditionNo) {
        return conditionRepository.findById(conditionNo)
                .orElseThrow(() -> new ConditionNotFoundException(conditionNo));
    }

    @Override
    public List<String> getRequiredFieldsByRule(Rule rule) {
        // Rule 엔티티에서 직접 조건 리스트 가져오기 (추가 쿼리 방지)
        return rule.getConditionList().stream()
                .map(Condition::getConField)
                .filter(conField -> conField != null && !conField.isEmpty()) // 유효성 검사
                .distinct() // 중복 제거
                .toList();
    }

    @Override
    public List<ConditionResult> evaluateConditionsForRule(Rule rule, Map<String, Object> facts) {
        List<Condition> conditionList = conditionRepository.findByRule(rule);

        return conditionList.stream()
                .map(cond -> new ConditionResult(
                        cond.getConditionNo(),
                        cond.getConField(),
                        cond.getConType(),
                        cond.getConValue(),
                        evaluateCondition(cond.getConditionNo(), facts)
                ))
                .toList();
    }

    private ConditionResponse toConditionResponse(Condition condition) {
        return new ConditionResponse(
                condition.getConditionNo(),
                condition.getRule().getRuleNo(),
                condition.getConType(),
                condition.getConField(),
                condition.getConValue(),
                condition.getConPriority()
        );
    }
}
