package com.nhnacademy.ruleengineservice.service.action.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionNotFoundException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import com.nhnacademy.ruleengineservice.registry.ActionHandlerRegistry;
import com.nhnacademy.ruleengineservice.repository.action.ActionRepository;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class ActionServiceImpl implements ActionService {

    private final ActionRepository actionRepository;

    private final RuleService ruleService;

    private final ActionHandlerRegistry actionHandlerRegistry;

    public ActionServiceImpl(ActionRepository actionRepository, RuleService ruleService, ActionHandlerRegistry actionHandlerRegistry) {
        this.actionRepository = actionRepository;
        this.ruleService = ruleService;
        this.actionHandlerRegistry = actionHandlerRegistry;
    }

    @Override
    public ActionResponse registerAction(ActionRegisterRequest request) {
        Rule rule = ruleService.getRuleEntity(request.getRuleNo());
        log.debug("registerAction rule : {}", rule);

        Action action = Action.ofNewAction(
                rule,
                request.getActType(),
                request.getActParam(),
                request.getActPriority()
        );
        log.debug("registerAction action : {}", action);

        return toActionResponse(actionRepository.save(action));
    }

    @Override
    public void deleteAction(Long actionNo) {
        if(!actionRepository.existsById(actionNo)) {
            log.error("deleteAction action not found");
            throw new ActionNotFoundException(actionNo);
        }

        actionRepository.deleteById(actionNo);
        log.debug("deleteAction success");
    }

    @Override
    @Transactional(readOnly = true)
    public ActionResponse getAction(Long actionNo) {
        log.debug("getAction start");

        return actionRepository.findById(actionNo)
                .map(this::toActionResponse)
                .orElseThrow(() -> new ActionNotFoundException(actionNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionResponse> getActionsByRule(Long ruleNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);

        List<Action> actionList = actionRepository.findByRule(rule);
        log.debug("getActionsByRule : {}", actionList);

        return actionList.stream()
                .map(this::toActionResponse)
                .toList();
    }

    @Override
    public ActionResult performAction(Long actionNo, Map<String, Object> context) {
        Action action = actionRepository.findById(actionNo)
                .orElseThrow(() -> new ActionNotFoundException(actionNo));

        try {
            ActionHandler handler = actionHandlerRegistry.getHandler(action.getActType());
            log.debug("performAction : {}", handler);

            return handler.handle(action, context);
        } catch (Exception e) {
            log.error("performAction fail!");

            return new ActionResult(
                    action.getActNo(),
                    false,
                    action.getActType(),
                    "액션 실행 중 오류: " + e.getMessage(),
                    null,
                    LocalDateTime.now()
            );
        }
    }

    @Override
    public List<ActionResult> executeActionsForRule(Rule rule, Map<String, Object> context) {
        // 1. 룰에 연결된 액션 목록 가져오기
        List<Action> actions = actionRepository.findByRule(rule);

        // 2. 각 액션 실행 후 결과 수집
        List<ActionResult> results = new ArrayList<>();
        for (Action action : actions) {
            ActionResult result = performAction(action.getActNo(), context);
            log.debug("executeActionsForRule : {}", result);

            results.add(result);
        }

        return results;
    }

    private ActionResponse toActionResponse(Action action) {
        return new ActionResponse(
                action.getActNo(),
                action.getRule().getRuleNo(),
                action.getActType(),
                action.getActParams(),
                action.getActPriority()
        );
    }
}
