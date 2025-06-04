package com.nhnacademy.ruleengineservice.service.parameter.impl;

import com.nhnacademy.ruleengineservice.domain.parameter.RuleParameter;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.parameter.ParameterRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.parameter.ParameterResponse;
import com.nhnacademy.ruleengineservice.exception.parameter.ParameterNotFoundException;
import com.nhnacademy.ruleengineservice.repository.parameter.RuleParameterRepository;
import com.nhnacademy.ruleengineservice.service.parameter.ParameterService;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@code ParameterServiceImpl}는 {@link ParameterService}의 구현체로,
 * 파라미터 관련 비즈니스 로직을 처리하기 위해 생성된 서비스 클래스입니다.
 * <p>
 * 현재 본 클래스는 구현되어 있으나, 실제 서비스에서는 사용되지 않습니다.
 * 추후 파라미터 기능 확장 또는 필요 시 활성화할 수 있습니다.
 * </p>
 *
 * @author 강승우
 * @since 1.0
 */
@Slf4j
@Service
@Transactional
public class ParameterServiceImpl implements ParameterService {

    private final RuleParameterRepository ruleParameterRepository;

    private final RuleService ruleService;

    public ParameterServiceImpl(RuleParameterRepository ruleParameterRepository, RuleService ruleService) {
        this.ruleParameterRepository = ruleParameterRepository;
        this.ruleService = ruleService;
    }

    @Override
    public ParameterResponse registerParameter(ParameterRegisterRequest request) {
        Rule rule = ruleService.getRuleEntity(request.getRuleNo());

        RuleParameter parameter = RuleParameter.ofNewRuleParameter(
                rule,
                request.getParamName(),
                request.getParamValue()
        );

        log.debug("registerParameter parameter : {}", parameter);

        return toParameterResponse(ruleParameterRepository.save(parameter));
    }

    @Override
    public void deleteParameter(Long paramNo) {
        if (!ruleParameterRepository.existsById(paramNo)) {
            log.error("deleteParameter parameter not found");
            throw new ParameterNotFoundException(paramNo);
        }

        ruleParameterRepository.deleteById(paramNo);
        log.debug("deleteParameter success");
    }

    @Override
    @Transactional(readOnly = true)
    public ParameterResponse getParameter(Long paramNo) {
        log.debug("getParameter start");

        return ruleParameterRepository.findById(paramNo)
                .map(this::toParameterResponse)
                .orElseThrow(() -> new ParameterNotFoundException(paramNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParameterResponse> getParametersByRule(Long ruleNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);

        List<RuleParameter> parameterList = ruleParameterRepository.findByRule(rule);
        log.debug("getParametersByRule : {}", parameterList);

        return parameterList.stream()
                .map(this::toParameterResponse)
                .toList();
    }

    @Override
    public void bindParameterValue(Long paramNo, String value) {
        RuleParameter parameter = ruleParameterRepository.findById(paramNo)
                .orElseThrow(() -> new ParameterNotFoundException(paramNo));

        parameter.setParamValue(value);
        log.debug("bindParameterValue : {}", parameter);
    }

    private ParameterResponse toParameterResponse(RuleParameter parameter) {
        return new ParameterResponse(
                parameter.getParamNo(),
                parameter.getRule().getRuleNo(),
                parameter.getParamName(),
                parameter.getParamValue()
        );
    }
}
