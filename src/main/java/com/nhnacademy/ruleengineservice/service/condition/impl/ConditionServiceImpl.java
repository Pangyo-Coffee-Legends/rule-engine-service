package com.nhnacademy.ruleengineservice.service.condition.impl;

import com.nhnacademy.ruleengineservice.domain.condition.Condition;
import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResponse;
import com.nhnacademy.ruleengineservice.dto.condition.ConditionResult;
import com.nhnacademy.ruleengineservice.exception.condition.ConditionNotFoundException;
import com.nhnacademy.ruleengineservice.repository.condition.ConditionRepository;
import com.nhnacademy.ruleengineservice.service.condition.ConditionService;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        return toConditionResponse(conditionRepository.save(condition));
    }

    @Override
    public void deleteCondition(Long conditionNo) {
        if(!conditionRepository.existsById(conditionNo)) {
            throw new ConditionNotFoundException(conditionNo);
        }

        conditionRepository.deleteById(conditionNo);
    }

    @Override
    @Transactional(readOnly = true)
    public ConditionResponse getCondition(Long conditionNo) {
        return conditionRepository.findById(conditionNo)
                .map(this::toConditionResponse)
                .orElseThrow(() -> new ConditionNotFoundException(conditionNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getConditionsByRule(Long ruleNo) {
        List<Condition> conditionList = conditionRepository.findAll();

        if (conditionList.isEmpty()) {
            throw new ConditionNotFoundException("Condition List Not Found");
        }

        return conditionList.stream()
                .map(this::toConditionResponse)
                .toList();
    }

    @Override
    public boolean evaluateCondition(Long conditionNo, Map<String, Object> facts) {
        Condition condition = conditionRepository.findById(conditionNo)
                .orElseThrow(() -> new ConditionNotFoundException(conditionNo));

        Object factValue = facts.get(condition.getConField());
        if (factValue == null) {
            return false;
        }

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
            case "IS_NULL":
                return factValue.toString().isEmpty();
            case "IS_NOT_NULL":
                return !factValue.toString().isEmpty();
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
        Set<String> requiredFields = new HashSet<>();

        // 룰에 연결된 조건에서 사용하는 필드 추출
        List<Condition> conditions = conditionRepository.findByRule(rule);
        for (Condition condition : conditions) {
            requiredFields.add(condition.getConField());
        }

        return new ArrayList<>(requiredFields);
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
