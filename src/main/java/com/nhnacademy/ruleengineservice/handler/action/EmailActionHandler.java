package com.nhnacademy.ruleengineservice.handler.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ruleengineservice.adaptor.NotifyAdaptor;
import com.nhnacademy.ruleengineservice.domain.action.Action;
import com.nhnacademy.ruleengineservice.dto.action.ActionResult;
import com.nhnacademy.ruleengineservice.dto.email.EmailRequest;
import com.nhnacademy.ruleengineservice.exception.action.ActionHandlerException;
import com.nhnacademy.ruleengineservice.handler.ActionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailActionHandler implements ActionHandler {

    private final NotifyAdaptor notifyAdaptor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String actType) {
        return "EMAIL".equals(actType);
    }

    @Override
    public ActionResult handle(Action action, Map<String, Object> context) throws ActionHandlerException {
        try {
            // JSON 파싱 (예외 발생 가능 지점)
            JsonNode emailParams = objectMapper.readTree(action.getActParams());

            // 필수 필드 검증
            String to = emailParams.required("to").asText();
            String subject = emailParams.required("subject").asText();
            String body = emailParams.required("body").asText();

            String type = "TEXT";
            if (emailParams.has("type")) {
                type = emailParams.get("type").asText();
            }

            EmailRequest request = new EmailRequest(to, subject, body, type);

            // 이메일 발송
            notifyAdaptor.sendEmail(request);

            return new ActionResult(
                    action.getActNo(),
                    true,
                    action.getActType(),
                    "이메일 발송 성공",
                    null,
                    LocalDateTime.now()
            );

        } catch (JsonProcessingException e) {
            throw new ActionHandlerException("이메일 파라미터 JSON 파싱 실패", e);
        } catch (IllegalArgumentException e) {
            throw new ActionHandlerException("필수 필드 누락: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ActionHandlerException("이메일 발송 실패", e);
        }
    }
}
