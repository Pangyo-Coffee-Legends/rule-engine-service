package com.nhnacademy.ruleengineservice.service.action.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.exception.action.ActionNotFoundException;
import com.nhnacademy.ruleengineservice.exception.rule.RuleNotFoundException;
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
import java.util.Objects;

/**
 * {@code ActionServiceImpl}는 {@link ActionService}의 구현체로,
 * 룰 엔진에서 액션(Action) 등록, 삭제, 조회, 실행 등 액션 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * 액션의 등록/삭제/조회는 물론, 액션 실행 요청 시 {@link ActionHandlerRegistry}에서
 * 적절한 핸들러를 찾아 액션을 실제로 수행하며, 실행 결과({@link ActionResult})를 반환합니다.
 * </p>
 *
 * <ul>
 *   <li>{@link #registerAction(ActionRegisterRequest)}: 액션 등록</li>
 *   <li>{@link #deleteAction(Long)}: 액션 단일 삭제</li>
 *   <li>{@link #deleteActionByRuleNoAndActionNo(Long, Long)}: 특정 룰에 속한 액션 삭제</li>
 *   <li>{@link #deleteActionByRule(Long)}: 룰에 속한 모든 액션 삭제</li>
 *   <li>{@link #getAction(Long)}: 액션 단일 조회</li>
 *   <li>{@link #getActionsByRule(Long)}: 룰에 속한 액션 목록 조회</li>
 *   <li>{@link #getActions()}: 전체 액션 목록 조회</li>
 *   <li>{@link #performAction(Long, Map)}: 액션 실행</li>
 *   <li>{@link #executeActionsForRule(Rule, Map)}: 룰에 속한 모든 액션 실행</li>
 * </ul>
 *
 * Spring의 {@code @Service} 및 {@code @Transactional} 어노테이션이 적용되어 있으며,
 * 내부적으로 {@code @Slf4j}를 사용해 실행 로그를 기록합니다.
 *
 * @author (작성자 이름)
 * @since 1.0
 */
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
    public void deleteActionByRuleNoAndActionNo(Long ruleNo, Long actionNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);
        if (Objects.isNull(rule)) {
            throw new RuleNotFoundException(ruleNo);
        }

        Action action = actionRepository.findById(actionNo)
                .orElseThrow(() -> new ActionNotFoundException(actionNo));

        if (!action.getRule().getRuleNo().equals(ruleNo)) {
            throw new IllegalArgumentException("Action does not belong to the specified rule");
        }

        actionRepository.delete(action);
        log.debug("Action {} associated with ruleNo = {} has been deleted.", actionNo, ruleNo);
    }

    @Override
    public void deleteActionByRule(Long ruleNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);

        if (Objects.isNull(rule)) {
            throw new RuleNotFoundException(ruleNo);
        }

        List<Action> actions = actionRepository.findByRule(rule);

        if (actions.isEmpty()) {
            throw new ActionNotFoundException("action is null");
        }

        actionRepository.deleteAll(actions);
        log.debug("{} actions associated with ruleNo = {} have been deleted.", ruleNo, actions.size());
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
    @Transactional(readOnly = true)
    public List<ActionResponse> getActions() {
        List<Action> actionList = actionRepository.findAll();

        log.debug("get action list : {}", actionList);
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
        log.info("[액션 실행] 룰 ID {} - 연결된 액션 {}개", rule.getRuleNo(), actions.size());

        // 2. 각 액션 실행 후 결과 수집
        List<ActionResult> results = new ArrayList<>();
        for (Action action : actions) {
            try {
                ActionResult result = performAction(action.getActNo(), context);
                results.add(result);
                log.info("[액션 성공] 액션 ID {} - 결과: {}", action.getActNo(), result);
            } catch (Exception e) {
                log.error("[액션 실패] 액션 ID {} - 오류: {}", action.getActNo(), e.getMessage());
            }
        }

        return results;
    }

    /**
     * Action 엔티티를 ActionResponse로 변환합니다.
     *
     * @param action 변환할 Action 엔티티
     * @return 변환된 ActionResponse 객체
     */
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
