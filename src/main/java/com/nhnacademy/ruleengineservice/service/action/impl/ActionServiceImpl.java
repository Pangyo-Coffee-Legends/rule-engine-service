package com.nhnacademy.ruleengineservice.service.action.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.time.LocalDateTime;
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
        String message;
        Object output = null;

        try {
            // 유형별 실행 로직 분기 (예시)
            switch (action.getActType()) {
                case "EMAIL":
                    try {
                        // JSON 파싱 시도 (예외 발생 가능)
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode emailParams = objectMapper.readTree(action.getActParams());

                        String to = emailParams.get("to").asText();
                        String subject = emailParams.get("subject").asText();
                        String body = emailParams.get("body").asText();

                        // 실제 이메일 발송 로직 (예: JavaMailSender 사용)
                        // emailService.send(to, subject, body);

                        success = true;
                        message = "이메일 발송 성공";
                    } catch (IOException e) {
                        throw new RuntimeException("이메일 파라미터 파싱 실패", e);
                    }
                    break;
                case "WEBHOOK":
                    // 외부 시스템으로 HTTP POST 요청 (예: context 에서 URL, payload 추출)
                    success = true;
                    message = "웹훅 호출 성공";
                    break;
                case "LOG":
                    // 시스템 로그 기록 (예: context 에서 로그 메시지 추출)
                    success = true;
                    message = "로그 기록 성공";
                    break;
                case "COMFORT_NOTIFICATION":
                    // context 에서 필요한 정보 추출
                    String location = (String) context.get("location");
                    LocalDateTime currentTime = LocalDateTime.now();
                    Double comfortIndex = (Double) context.get("comfortIndex");
                    String comfortGrade = (String) context.get("comfortGrade");

                    // DTO 생성 및 데이터 설정
                    ComfortInfoDTO comfortInfo = new ComfortInfoDTO(
                            location,
                            currentTime,
                            comfortIndex,
                            comfortGrade
                    );

                    success = true;
                    message = "쾌적도 알림 전송 성공";
                    output = comfortInfo;
                    break;
                default:
                    message = "지원하지 않는 액션 타입";
            }
        } catch (Exception e) {
            message = "액션 실행 중 오류: " + e.getMessage();
        }

        return new ActionResult(
                action.getActNo(),
                success,
                action.getActType(),
                message,
                output,
                LocalDateTime.now()
        );
    }

    @Override
    public List<ActionResult> executeActionsForRule(Rule rule, Map<String, Object> context) {
        // 1. 룰에 연결된 액션 목록 가져오기
        List<Action> actions = actionRepository.findByRule(rule);

        // 2. 각 액션 실행 후 결과 수집
        List<ActionResult> results = new ArrayList<>();
        for (Action action : actions) {
            ActionResult result = performAction(action.getActNo(), context);
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
