package com.nhnacademy.ruleengineservice.service.action.impl;

import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.domain.rule.Rule;
import com.nhnacademy.ruleengineservice.dto.action.ActionRegisterRequest;
import com.nhnacademy.ruleengineservice.dto.action.ActionResponse;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.comfort.ComfortInfoDTO;
import com.nhnacademy.ruleengineservice.exception.action.ActionNotFoundException;
import com.nhnacademy.ruleengineservice.repository.action.ActionRepository;
import com.nhnacademy.ruleengineservice.service.action.ActionService;
import com.nhnacademy.ruleengineservice.service.rule.RuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ActionServiceImpl implements ActionService {

    private final ActionRepository actionRepository;

    private final RuleService ruleService;

    public ActionServiceImpl(ActionRepository actionRepository, RuleService ruleService) {
        this.actionRepository = actionRepository;
        this.ruleService = ruleService;
    }

    @Override
    public ActionResponse registerAction(ActionRegisterRequest request) {
        Rule rule = ruleService.getRuleEntity(request.getRuleNo());

        Action action = Action.ofNewAction(
                rule,
                request.getActType(),
                request.getActParam(),
                request.getActPriority()
        );

        return toActionResponse(actionRepository.save(action));
    }

    @Override
    public void deleteAction(Long actionNo) {
        if(!actionRepository.existsById(actionNo)) {
            throw new ActionNotFoundException(actionNo);
        }

        actionRepository.deleteById(actionNo);
    }

    @Override
    @Transactional(readOnly = true)
    public ActionResponse getAction(Long actionNo) {
        return actionRepository.findById(actionNo)
                .map(this::toActionResponse)
                .orElseThrow(() -> new ActionNotFoundException(actionNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionResponse> getActionsByRule(Long ruleNo) {
        Rule rule = ruleService.getRuleEntity(ruleNo);

        List<Action> actionList = actionRepository.findByRule(rule);

        return actionList.stream()
                .map(this::toActionResponse)
                .toList();
    }

    @Override
    public ActionResult performAction(Long actionNo, Map<String, Object> context) {
        Action action = actionRepository.findById(actionNo)
                .orElseThrow(() -> new ActionNotFoundException(actionNo));

        boolean success = false;
        String message = "";
        Object output = null;

        try {
            // 유형별 실행 로직 분기 (예시)
            switch (action.getActType()) {
                case "EMAIL":
                    // 이메일 발송 로직 (예: context 에서 수신자, 제목, 본문 추출)
                    // output = emailService.sendEmail(...);
                    success = true;
                    message = "이메일 발송 성공";
                    break;
                case "WEBHOOK":
                    // 외부 시스템으로 HTTP POST 요청 (예: context 에서 URL, payload 추출)
                    // output = webhookService.sendWebhook(...);
                    success = true;
                    message = "웹훅 호출 성공";
                    break;
                case "LOG":
                    // 시스템 로그 기록 (예: context 에서 로그 메시지 추출)
                    // output = logService.writeLog(...);
                    success = true;
                    message = "로그 기록 성공";
                    break;
                case "NOTIFICATION":
                    // 사내/외부 알림 시스템 연동 (예: context 에서 알림 대상, 메시지 추출)
                    // output = notificationService.sendNotification(...);
                    success = true;
                    message = "알림 전송 성공";
                    break;
                case "COMFORT_NOTIFICATION":
                    // context 에서 필요한 정보 추출
                    String location = (String) context.get("location");
                    Double comfortIndex = (Double) context.get("comfortIndex");
                    String comfortGrade = (String) context.get("comfortGrade");

                    // DTO 생성 및 데이터 설정
                    ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                            location,
                            comfortIndex,
                            comfortGrade
                    );

                    // 실제 알림 로직 구현
                    // notificationService.sendComfortNotification(comfortInfo);

                    success = true;
                    message = "쾌적도 알림 전송 성공";
                    output = comfortInfo;
                    break;
                default:
                    message = "지원하지 않는 액션 타입";
            }
        } catch (Exception e) {
            message = "액션 실행 중 오류: " + e.getMessage();
            output = null;
        }

        return ActionResult.ofNewActionResult(
                action.getActNo(),
                success,
                action.getActType(),
                message,
                output
        );
    }

    @Override
    public List<ActionResult> executeActionsForRule(Rule rule, Map<String, Object> context) {
        // 1. 룰에 연결된 액션 목록 가져오기
        List<Action> actions = actionRepository.findByRule(rule);

        // 2. 각 액션 실행 후 결과 수집
        List<ActionResult> results = new ArrayList<>();
        for (Action action : actions) {
            // 우선순위에 따라 정렬하거나 처리할 수 있음
            ActionResult result = performAction(action.getActNo(), context);
            results.add(result);

            // 선택적: 액션 실행 실패 시 중단 옵션
            // if (!result.isSuccess() && rule.isStopOnFailure()) {
            //     break;
            // }
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
